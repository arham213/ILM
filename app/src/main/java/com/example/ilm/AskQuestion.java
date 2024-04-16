package com.example.ilm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AskQuestion extends AppCompatActivity {
    Button send_button;
    EditText question;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question);

        //initialize variables
        send_button=(Button) findViewById(R.id.send_question_button);
        question=(EditText) findViewById(R.id.ask_question_editText);
        send_button.setOnClickListener(button_listener);
    }

    private View.OnClickListener button_listener=new View.OnClickListener(){
        public void onClick(View v){
            if(isInternetConnected(AskQuestion.this)) {
                Intent intent = new Intent();
                intent.putExtra("question", question.getText().toString());
                setResult(RESULT_OK, intent);
                Toast.makeText(AskQuestion.this,"Question Sent Successfully",Toast.LENGTH_SHORT);
                finish();
            }
            else{
                Toast.makeText(AskQuestion.this,"Please check your internet connection",Toast.LENGTH_SHORT);
            }
        }
    };

    public boolean isInternetConnected(Context c) {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
}
