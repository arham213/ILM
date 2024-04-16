package com.example.ilm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    Button signup_button;
    Button login_button;

    ConnectivityChangeReciever connectivityChangeReciever=new ConnectivityChangeReciever();

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

    protected void onCreate(Bundle savedINstanceState){
        super.onCreate(savedINstanceState);
        setContentView(R.layout.home_page);

        //initialize variables
        signup_button=(Button) findViewById(R.id.signup_button);
        login_button=(Button) findViewById(R.id.login_button);
        /*signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePage.this, Signup.class);
                startActivity(intent);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePage.this, Login.class);
                startActivity(intent);
            }
        });*/
    }

    private View.OnClickListener login_listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(HomePage.this, Login.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener signup_listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(HomePage.this, Signup.class);
            startActivity(intent);
        }
    };

    public class ConnectivityChangeReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent i){
            if(isInternetConnected(HomePage.this)) {
                login_button.setOnClickListener(login_listener);
                signup_button.setOnClickListener(signup_listener);
            }
            else{
                login_button.setOnClickListener(dummy_listener);
                signup_button.setOnClickListener(dummy_listener);
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
            public void onClick(View v) {
                Toast.makeText(HomePage.this,"Please Check Your Internet Connection",Toast.LENGTH_SHORT);
            }
        };
    }
}
