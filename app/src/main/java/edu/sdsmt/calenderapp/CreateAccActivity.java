package edu.sdsmt.calenderapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class CreateAccActivity extends AppCompatActivity {

    private TextView txt_name;
    private TextView txt_username;
    private TextView txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc_layout);

        txt_name = findViewById(R.id.txt_new_name);
        txt_username= findViewById(R.id.txt_new_username);
        txt_password= findViewById(R.id.txt_new_password);
    }

    public void onButtonClick(View view){


        String name = txt_name.getText().toString();
        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();

        //Check if all the details are filled
        if(name.equals("") || username.equals("") || password.equals("")){
            AlertDialog alertDialog;
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("All the fields should be filled");
            alertDialog.show();

        }else {
            String type = "create";
            BackgroundWorker bgworker = new BackgroundWorker(this);
            bgworker.execute(type, username, password, name);
            //finish();
        }
    }

    public void onCancel(View view)
    {
        finish();
    }
}
