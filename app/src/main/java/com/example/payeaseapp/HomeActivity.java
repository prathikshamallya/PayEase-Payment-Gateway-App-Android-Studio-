package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    private DBHandler dbHandler; // Declare a reference to the DBHandler

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHandler = new DBHandler(this); // Create an instance of DBHandler to open the database

        ImageView to_phone = (ImageView) findViewById(R.id.tophone);
        ImageView to_contact = (ImageView) findViewById(R.id.tocontact);
        ImageView to_bank = (ImageView) findViewById(R.id.tobank);
        ImageButton menu = (ImageButton) findViewById(R.id.menu);
        //ImageView digitalwallet = (ImageButton) findViewById(R.id.digitalwallet);
        ImageView balance_history = (ImageView) findViewById(R.id.balance);
        ImageView electricityRbtn = (ImageView) findViewById(R.id.electricityRbtn);
        ImageView dthRbtn = (ImageView) findViewById(R.id.dthRbtn);
        ImageView mobileRbtn = (ImageView) findViewById(R.id.mobileRbtn);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfilePageActivity.class);
                startActivity(intent);
            }
        });


        to_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(HomeActivity.this, ToPhoneActivity.class);
                startActivity(i1);
            }
        });

        to_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(HomeActivity.this, ToContactsActivity.class);
                startActivity(i2);
            }
        });

        to_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(HomeActivity.this, ToBankActivity.class);
                startActivity(i3);
            }
        });

//        digitalwallet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i4 = new Intent(HomeActivity.this, DigitalWalletActivity.class);
//                startActivity(i4);
//            }
//        });

        balance_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i5 = new Intent(HomeActivity.this, Balance_History_Activity.class);
                startActivity(i5);
            }
        });
        electricityRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i6 = new Intent(HomeActivity.this, ElectricityRecharge.class);
                startActivity(i6);
            }
        });
        dthRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i7 = new Intent(HomeActivity.this, DthRecharge.class);
                startActivity(i7);
            }
        });

        mobileRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i8 = new Intent(HomeActivity.this, MobileRechargeActivity.class);
                startActivity(i8);
            }
        });
    }
}