package com.example.ilm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnswerQuestion extends AppCompatActivity {
    String id;
    EditText answer;
    TextView question;


    protected void onCreate(Bundle savedInsatnceState){
        super.onCreate(savedInsatnceState);
        setContentView(R.layout.answer_question);

        answer=(EditText) findViewById(R.id.answer_quesion_editText);
        question=(TextView) findViewById(R.id.question);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        question.setText(intent.getStringExtra("question"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.answer_question,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.answer_question) {
            if(isInternetConnected(this)) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("questions");
                reference.child(id).child("answer").setValue(answer.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.putExtra("answer", answer.getText().toString());
                setResult(RESULT_OK);
                finish();
            }
            else{
                Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT);
            }
        }
        return super.onOptionsItemSelected(item);
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
