package com.example.androidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter= 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);
        Info = (TextView)findViewById(R.id.tvInfo);
        Login = (Button)findViewById(R.id.btn);

        Info.setText("No of attempts remaining: 5");
		//when button is clickd invokes following callback
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//get text enter as username and password and call validate()
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String userName, String userPassword){
		//if username and password equal string then launches new activity. else reduces counter by one
        if((userName.equals("user")) && (userPassword.equals("user"))){
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        }else{
            counter--;
            Info.setText("No of attempts remaining:" + String.valueOf(counter));
			//disables the button id counter reaches 0
            if(counter==0){
                Login.setEnabled(false);
            }
        }
    }
}
