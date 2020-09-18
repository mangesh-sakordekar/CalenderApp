package edu.sdsmt.calenderapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private TextView txt_username;
    private TextView txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_username= findViewById(R.id.txt_username);
        txt_password= findViewById(R.id.txt_password);
    }

    public void onButtonClick(View view){


        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();
        if(username.equals("") || password.equals("")){
            AlertDialog alertDialog;
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("All the fields should be filled");
            alertDialog.show();

        }
        else {
            String type = "login";
            BackgroundWorker bgworker = new BackgroundWorker(this);
            bgworker.execute(type, username, password);
        }

    }

    public void onCreateAccount(View view){
        Intent newIntent = new Intent(getApplicationContext(), CreateAccActivity.class);
        startActivity(newIntent);
    }
}
