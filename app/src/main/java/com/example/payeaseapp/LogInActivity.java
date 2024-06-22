package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class LogInActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "login_prefs";
    public static final String USERNAME_KEY = "username_key";
    public static final String PASSWORD_KEY = "password_key";

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        EditText uname = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);

        MaterialButton loginBtn = findViewById(R.id.loginbtn);



        DBHandler dbHandler = new DBHandler(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unameText = uname.getText().toString();
                String passText = pass.getText().toString();

                if (unameText.isEmpty() || passText.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please enter all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isValidUser = dbHandler.isUserValid(unameText, passText);

                    if (isValidUser) {
                        sharedPreferences.edit().putString(USERNAME_KEY, unameText).apply();
                        sharedPreferences.edit().putString(PASSWORD_KEY, passText).apply();

                        Intent i = new Intent(LogInActivity.this, HomeActivity.class);
                        finish();
                        startActivity(i);
                    } else {
                        Toast.makeText(LogInActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        ImageButton backbtn = (ImageButton) findViewById(R.id.backBtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}