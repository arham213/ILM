package com.example.ilm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Admin extends AppCompatActivity implements AdminAdapter.QuestionItemClickListener {

    ArrayList<Question> questions;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private AdminAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ActivityResultLauncher<Intent> launcher;

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

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        //initialize variables
        dao = new QuestionFirebaseDAO();
        dao.setObserver(new QuestionFirebaseDAO.DataObserver() {
            @Override
            public void update() {
                refresh();
            }
        });

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
        mAdapter=new AdminAdapter(questions,this);
        adapter=mAdapter;

        //set layout manager
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        //set adapter
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //
        filterable= mAdapter;

        launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent data=result.getData();
                    temp(data);
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

    private void temp(Intent data){
        String id=data.getStringExtra("id");
        String answer=data.getStringExtra("answer");
        Question q=getQuestion(id);
        if(q!=null) {
            q.save(id, answer);
            Toast.makeText(Admin.this,"Answer Saved Successfully",Toast.LENGTH_SHORT);
        }
    }

    private Question getQuestion(String id){
        if(questions!=null){
            for(Question q:questions){
                if(q.getId().equals(id)){
                    return q;
                }
            }
        }
        return null;
    }

    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("data",questions);
    }

    @Override
    public void onClick(Question q) {
        Intent intent = new Intent(this, AnswerQuestion.class);
        intent.putExtra("id",q.getId());
        intent.putExtra("question",q.getQuestion());
        launcher.launch(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_page,menu);
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
        else if(item.getItemId()==R.id.Logout){
            finish();
            Intent intent=new Intent(this,HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        QuestionViewModel vm = new ViewModelProvider(this).get(QuestionViewModel.class);
        vm.setDao(dao);
        ArrayList<Question> tempData;
        tempData = vm.update();
        questions=getUnansweredQuestions(tempData);
        if (questions != null){
            mAdapter.updateData(questions);
        }
    }

    private ArrayList<Question> getUnansweredQuestions(ArrayList<Question> tempData){
        ArrayList<Question> questions=new ArrayList<Question>();
        for(Question q:tempData){
            if(q.getAnswer().equals("null")){
                questions.add(q);
            }
        }
        return questions;
    }

    public class ConnectivityChangeReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent i) {
            if (!isInternetConnected(Admin.this)) {
                Toast.makeText(Admin.this, "No Internet Connection",Toast.LENGTH_SHORT);
            }

        }

        public boolean isInternetConnected(Context c) {
            try {
                String command = "ping -c 1 google.com";
                return (Runtime.getRuntime().exec(command).waitFor() == 0);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
