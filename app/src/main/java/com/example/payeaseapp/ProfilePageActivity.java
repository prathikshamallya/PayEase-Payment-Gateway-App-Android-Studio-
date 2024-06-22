package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {
    TextView userText, emailText, phoneText, bankAcc, bankName, ifscC, balance;
    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    String user_str, email_str, phone;
    public static final String SHARED_PREFS = "login_prefs";
    public static final String USERNAME_KEY = "username_key";
    public static final String PHONE_KEY = "phone_key";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        dbHandler = new DBHandler(this);
        userText = findViewById(R.id.userText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        bankAcc = findViewById(R.id.bankAccText);
        bankName = findViewById(R.id.bankNameText);
        ifscC = findViewById(R.id.ifscText);
        balance = findViewById(R.id.balanceText);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        user_str = sharedPreferences.getString(USERNAME_KEY, "");
        email_str = dbHandler.getEmail(this);
        phone = dbHandler.getPhone(this);

        userText.setText("Username: "+user_str);
        emailText.setText("Email: " +email_str);
        phoneText.setText("Phone: " +phone);

        ArrayList<String> userDetails = dbHandler.getBankDetails(this);

        if (!userDetails.isEmpty()) {
            String bankAccountNumber = userDetails.get(0);
            String bankName2 = userDetails.get(1);
            String ifscCode = userDetails.get(2);
            String balance2 = userDetails.get(3);

            bankAcc.setText("Bank Account Number: " +bankAccountNumber);
            bankName.setText("Bank Name: " +bankName2);
            ifscC.setText("IFSC Code: " +ifscCode);
            balance.setText("Balance: " +balance2);
        } else {
            Toast.makeText(ProfilePageActivity.this, "Bank Details Missing.", Toast.LENGTH_SHORT).show();
        }

        ListView listView = findViewById(R.id.plist);
        List<String> list = new ArrayList<>();
        list.add("Edit Profile");
        list.add("Delete Account");
        list.add("Log Out");

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent i1 = new Intent(ProfilePageActivity.this, EditProfileActivity.class);
                    i1.putExtra("username", user_str);
                    startActivity(i1);
                }
                if(position == 1)
                {
                    builder1.setMessage("Are you sure you want to Delete Account?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    dbHandler.deleteUserByUsername(user_str);
                                    Intent i3 = new Intent(ProfilePageActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(i3);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder1.create();
                    alertDialog.setTitle("PayEase");
                    alertDialog.show();
                }
                if(position == 2)
                {
                    builder1.setMessage("Log out of " +user_str+ " ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    Intent i2 = new Intent(ProfilePageActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(i2);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder1.create();
                    alertDialog.setTitle("PayEase");
                    alertDialog.show();
                }
            }
        });

        ImageButton backP = (ImageButton) findViewById(R.id.backprofilebtn);
        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(ProfilePageActivity.this, HomeActivity.class);
                startActivity(i3);
            }
        });
    }
}