package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddBankActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_BANK = "bank_prefs";
    public static final String BANK_ACC_NUM_KEY = "Bank_Acc_Num_key";
    public static final String BANK_NAME = "bank_name_key";
    public static final String IFSC_CODE = "ifsc_key";
    public static final String BALANCE_KEY = "balance_key";
    public static final String UPI_KEY = "upi_key";

    SharedPreferences sharedPreferences;

    EditText bankName, BankaccNum, ifscC, holderName, upiPin, balanceAmt;
    Button addbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);

        DBHandler dbHandler = new DBHandler(this);

        bankName = findViewById(R.id.bankName);
        BankaccNum = findViewById(R.id.BankaccNum);
        ifscC = findViewById(R.id.ifscC);
        holderName = findViewById(R.id.holderName);
        upiPin = findViewById(R.id.upiPin);
        balanceAmt = findViewById(R.id.balanceAmt);
        addbtn = findViewById(R.id.addbtn);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bankName1 = bankName.getText().toString();
                String bankAccName1 = holderName.getText().toString();
                String bankAccNum = BankaccNum.getText().toString();
                String upiPin1 = upiPin.getText().toString();
                String ifscCode1 = ifscC.getText().toString();
                String balance1 = balanceAmt.getText().toString();

                if(bankName1.isEmpty() && bankAccName1.isEmpty() && bankAccNum.isEmpty() && upiPin1.isEmpty() && ifscCode1.isEmpty() && balance1.isEmpty())
                {
                    Toast.makeText(AddBankActivity.this, "Please enter all the required fields.",Toast.LENGTH_SHORT).show();
                }
                else {
                    dbHandler.addBankDetails(bankAccNum, bankAccName1, bankName1, upiPin1, ifscCode1, balance1);
                    Toast.makeText(AddBankActivity.this, "Bank Details successfully added.",Toast.LENGTH_SHORT).show();
                    BankaccNum.setText("");
                    bankName.setText("");
                    ifscC.setText("");
                    holderName.setText("");
                    upiPin.setText("");
                    balanceAmt.setText("");

                    sharedPreferences = getSharedPreferences(SHARED_PREFS_BANK, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(BANK_ACC_NUM_KEY, bankAccNum).apply();
                    sharedPreferences.edit().putString(BANK_NAME, bankName1).apply();
                    sharedPreferences.edit().putString(IFSC_CODE, ifscCode1).apply();
                    sharedPreferences.edit().putString(BALANCE_KEY, balance1).apply();

                    saveMessage(bankAccNum, bankName1, ifscCode1, balance1, upiPin1);

                    Intent intent = new Intent(AddBankActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        ImageButton backbtnBank = (ImageButton) findViewById(R.id.backbtnBank);
        backbtnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void saveMessage(String acc_num, String bank_name, String ifsc, String bal, String upi)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BANK_ACC_NUM_KEY, acc_num);
        editor.putString(BANK_NAME, bank_name);
        editor.putString(IFSC_CODE, ifsc);
        editor.putString(BALANCE_KEY, bal);
        editor.putString(UPI_KEY, upi);
        editor.apply();
    }
}