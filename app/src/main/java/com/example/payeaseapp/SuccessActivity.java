package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    ImageButton backS;

    Button done;
    TextView receiver, amtPaid, updateBalance, sender;
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "login_prefs";
    public static final String USERNAME_KEY = "username_key";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        receiver = findViewById(R.id.receiver);
        amtPaid = findViewById(R.id.paidAmt);
        updateBalance = findViewById(R.id.updatedBal);
        backS = findViewById(R.id.backSuccess);
        sender = findViewById(R.id.sender);
        done = findViewById(R.id.done);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String user_str = sharedPreferences.getString(USERNAME_KEY, "");

        Intent i1 = getIntent();
        String username = i1.getStringExtra("Username");
        String amtPaid1 = i1.getStringExtra("AmountPaid");
        String updateB = i1.getStringExtra("UpdatedBalance");

        sender.setText("Sent By: " +user_str);
        receiver.setText("Sent to: " +username);
        amtPaid.setText("Transfer Amount: " +amtPaid1);
        updateBalance.setText("Updated Balance: " +updateB);

        backS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuccessActivity.this, HomeActivity.class);
                finish();
                startActivity(i);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuccessActivity.this, HomeActivity.class);
                finish();
                startActivity(i);
            }
        });
    }
}