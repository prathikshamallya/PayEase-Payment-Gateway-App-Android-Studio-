package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class DigitalWalletActivity extends AppCompatActivity {

    private TextView walletAmountTextView;
    private EditText addMoneyEditText;
    private DBHandler dbHandler;

    ImageButton backwallet;

    MaterialButton proceedbtn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_wallet);

        walletAmountTextView = findViewById(R.id.walletamount);
        addMoneyEditText = findViewById(R.id.addmoney);
        dbHandler = new DBHandler(this);
        backwallet = findViewById(R.id.backwallet);
        proceedbtn = findViewById(R.id.proceedbtn);

        backwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(DigitalWalletActivity.this, HomeActivity.class);
                startActivity(i1);
            }
        });

        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
