package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class FailureActivity extends AppCompatActivity {

    ImageButton backbtn;
    TextView amt, msg;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure);

        amt = findViewById(R.id.amtP);
        msg = findViewById(R.id.msg);
        Intent i2 = getIntent();
        String amt1 = i2.getStringExtra("AmountPaid");
        String phone = i2.getStringExtra("Result");

        amt.setText("Amount to be Paid: " +amt1);
        msg.setText("No User found of Phone Number: " +phone);

        backbtn = findViewById(R.id.backFailure);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FailureActivity.this, ToPhoneActivity.class);
                finish();
                startActivity(i);
            }
        });
    }
}