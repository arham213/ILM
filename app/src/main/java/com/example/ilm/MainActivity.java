package com.example.ilm;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements QuestionAdapter.QuestionItemClickListener{
    ArrayList<Question> questions;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private QuestionAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ActivityResultLauncher<Intent> launcher;
    Button ask_button;
    EditText search;
    Filterable filterable;
    private int displayMode;
    IQuestionDAO dao;
    ConnectivityChangeReciever connectivityChangeReciever=new ConnectivityChangeReciever();

    // constants
    private static final int LIST_DISPLAY = 1;
    private static final int GRID_DISPLAY = 2;
    private static final String DISPLAY_KEY = "display";
    private static final String DATA_KEY = "data";

    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter filter=new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityChangeReciever,filter);
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(connectivityChangeReciever);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize variables
        dao = new QuestionFirebaseDAO();
        dao.setObserver(new QuestionFirebaseDAO.DataObserver() {
            @Override
            public void update() {
                refresh();
            }
        });

        ask_button=(Button) findViewById(R.id.ask_question_button);
        //ask_button.setOnClickListener(button_listener);
        search=(EditText) findViewById(R.id.search_view);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterable.getFilter().filter(search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        QuestionViewModel vm=new ViewModelProvider(this).get(QuestionViewModel.class);
        vm.setDao(dao);
        questions=vm.getNotes(savedInstanceState,DATA_KEY);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        layoutManager=new LinearLayoutManager(this);
        mAdapter=new QuestionAdapter(questions,this);
        adapter=mAdapter;

        //set layout manager
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        //set adapter
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //
        filterable= mAdapter;

        //register Launcher
        launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK) {
                    Intent data1 = result.getData();
                    String question = data1.getStringExtra("question");
                    Question q = new Question();
                    if (!question.equals("")) {
                        if (question.length() < 20) {
                            q.setTitle(question);
                        } else {
                            q.setTitle(question.substring(0, 20));
                        }
                        q.setQuestion(question);
                        //q.setAnswer("ASSALAMO ALAIKUM WA RAHMATULLAH I WA BRAKATUH! Please Wait for the Answer!");
                        q.setAnswer("null");
                        //questions.add(0, q);
                        q.setDao(dao);
                        q.save();
                        //adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // display mode
        if( savedInstanceState != null){
            displayMode = savedInstanceState.getInt(DISPLAY_KEY,LIST_DISPLAY);
        }
        else {
            displayMode = LIST_DISPLAY;
        }
    }

    private View.OnClickListener button_listener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(view.getContext(),AskQuestion.class);
            launcher.launch(intent);
        }
    };

    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("data",questions);
    }

    @Override
    public void onClick(Question q) {
        Intent intent = new Intent(this, ViewQuestion.class);
        intent.putExtra("title",q.getTitle());
        intent.putExtra("question",q.getQuestion());
        intent.putExtra("answer",q.getAnswer());
        //index.put(n.getId(),n);
        launcher.launch(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_page,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.display){
            if (displayMode == LIST_DISPLAY) {
                layoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(layoutManager);
                item.setIcon(R.drawable.ic_baseline_list_24);
                displayMode = GRID_DISPLAY;
            } else if (displayMode == GRID_DISPLAY){
                layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                item.setIcon(R.drawable.ic_baseline_grid_on_24);
                displayMode = LIST_DISPLAY;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        QuestionViewModel vm = new ViewModelProvider(this).get(QuestionViewModel.class);
        vm.setDao(dao);
        ArrayList<Question> tempData;
        tempData = vm.update();
        questions=getAnsweredQuestions(tempData);
        if (questions != null){
            mAdapter.updateData(questions);
        }
    }

    private ArrayList<Question> getAnsweredQuestions(ArrayList<Question> tempData){
        ArrayList<Question> questions=new ArrayList<Question>();
        for(Question q:tempData){
            if(!q.getAnswer().equals("null")){
                questions.add(q);
            }
        }
        return questions;
    }

    public class ConnectivityChangeReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent i){
            if(isInternetConnected(MainActivity.this)) {
                //MainActivity.this.finish();
                ask_button.setOnClickListener(button_listener);
            }
            else{
                ask_button.setOnClickListener(dummy_listener);
                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        }

        public boolean isInternetConnected(Context c){
            try {
                String command = "ping -c 1 google.com";
                return (Runtime.getRuntime().exec(command).waitFor() == 0);
            } catch (Exception e) {
                return false;
            }
        }

        private View.OnClickListener dummy_listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        };
    }
}

