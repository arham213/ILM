package com.example.ilm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewQuestion extends AppCompatActivity {

    TextView title;
    TextView question;
    TextView answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_question);

        //initialize variables
        title=(TextView) findViewById(R.id.question_title);
        question=(TextView) findViewById(R.id.question);
        question.setBackgroundColor(0xc2bbbf);
        answer=(TextView) findViewById(R.id.answer);

        Intent intent=getIntent();
        title.setText(intent.getStringExtra("title"));
        question.setText(intent.getStringExtra("question"));
        answer.setText(intent.getStringExtra("answer"));
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.view_question,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.share){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, title.getText().toString()+"\n\n"+
                    question.getText().toString()+"\n\n"+answer.getText().toString());
            intent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(intent, null);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                throw new RuntimeException("Application not Found",ex);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
