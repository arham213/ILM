package com.example.ilm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    Button login_button;
    EditText username,password;

    protected void onCreate(Bundle savedINstanceState){
        super.onCreate(savedINstanceState);
        setContentView(R.layout.login_page);

        //initialize variables
        login_button=(Button) findViewById(R.id.login_button);
        username=(EditText) findViewById(R.id.username_editText);
        password=(EditText) findViewById(R.id.password_editText);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//admin view
                if(isInternetConnected(Login.this)) {
                    if (username.getText().toString().equals("arham") && password.getText().toString().equals("arh213.,")) {
                        Intent intent = new Intent(Login.this, Admin.class);
                        startActivity(intent);
                    } else {//normal user view
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(Login.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isInternetConnected(Context c){
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
}
