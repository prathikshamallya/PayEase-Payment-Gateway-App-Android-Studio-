package com.example.payeaseapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToBankActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText accNumberEditText, upiB, ifscCodeEditText, accHolderNameEditText, amountEditText;
    private Button confirmButton;

    public static final String SHARED_PREFS = "login_prefs";
    public static final String USERNAME_KEY = "username_key";

    DBHandler dbHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_bank);

        backButton = findViewById(R.id.backBank);
        accNumberEditText = findViewById(R.id.acc_number);
        upiB = findViewById(R.id.upiB);
        ifscCodeEditText = findViewById(R.id.ifsc_code);
        accHolderNameEditText = findViewById(R.id.acc_holder_name);
        amountEditText = findViewById(R.id.amount);
        confirmButton = findViewById(R.id.confrmbtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick(v);
            }
        });
    }

    public void onConfirmClick(View view) {
        String accNumber = accNumberEditText.getText().toString();
        String upiPin = upiB.getText().toString();
        String ifscCode = ifscCodeEditText.getText().toString();
        String accHolderName = accHolderNameEditText.getText().toString();
        String amount = amountEditText.getText().toString();
        String upi = dbHandler.getUpiPin(ToBankActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PayEaseApp";
            String desc = "Payment App";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("n001", name, imp);
            channel.setDescription(desc);
            NotificationManager nManager = getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(channel);
        }

        if(upiPin.equals(upi)) {
            if (validateInput(accNumber, upiPin, ifscCode, accHolderName, amount)) {
                // Retrieve logged-in user's username
                DBHandler dbHelper = new DBHandler(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                String user_str = sharedPreferences.getString(USERNAME_KEY, "");

                // Retrieve user's bank account details based on the username
                String[] columns = {"BANK_ACC_NO", "BALANCE"};
                String selection = "BANK_ACC_NAME = ?";
                String[] selectionArgs = {user_str};
                Cursor cursor = db.query("payEaseBank", columns, selection, selectionArgs, null, null, null);

                if (cursor.moveToFirst()) {
                    int balance1index = cursor.getColumnIndex("BALANCE");

                    if (balance1index >= 0) {
                        double balance1 = Double.parseDouble(cursor.getString(balance1index));

                        // Retrieve balance2 for the target bank account based on the provided details

                        String selectBalance2Query = "SELECT BALANCE FROM payEaseBank WHERE BANK_ACC_NO = ? AND IFSC_CODE = ?";
                        Cursor cursor2 = db.rawQuery(selectBalance2Query, new String[]{accNumber, ifscCode});

                        if (cursor2 != null && cursor2.moveToFirst()) {
                            double balance2 = Double.parseDouble(cursor2.getString(0));
                            cursor2.close();

                            // Validate if the user has enough balance for the transaction
                            double transactionAmount = Double.parseDouble(amount);
                            if (balance1 >= transactionAmount) {
                                // Perform the transaction
                                balance1 -= transactionAmount;
                                balance2 += transactionAmount;

                                // Update the balances in the database
                                ContentValues values1 = new ContentValues();
                                values1.put("BALANCE", balance1);
                                db.update("payEaseBank", values1, "BANK_ACC_NAME = ?", new String[]{user_str});

                                ContentValues values2 = new ContentValues();
                                values2.put("BALANCE", balance2);
                                db.update("payEaseBank", values2, "BANK_ACC_NO = ? AND IFSC_CODE = ?", new String[]{accNumber, ifscCode});

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = dateFormat.format(new Date());
                                ContentValues transactionValues = new ContentValues();
                                transactionValues.put("userName", user_str);
                                transactionValues.put("BALANCE", balance1);
                                transactionValues.put("receiver", accHolderName);
                                transactionValues.put("deduct", amount);
                                transactionValues.put("timedate", formattedDate);
                                db.insert("payEaseBTransaction", null, transactionValues);


                                // Log the transaction
                                logTransaction(user_str, balance1, accNumber, transactionAmount);
                                String bal1 = Double.toString(balance1);

                                Intent i1 = new Intent(ToBankActivity.this, SuccessActivity.class);
                                i1.putExtra("Username", accHolderName);
                                i1.putExtra("AmountPaid", amount);
                                i1.putExtra("UpdatedBalance", bal1);
                                startActivity(i1);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(ToBankActivity.this, "n001")
                                        .setSmallIcon(R.drawable.payease_splashscreen)
                                        .setContentTitle("Bank Payment Transaction")
                                        .setContentText("Payment done successfully to " +accHolderName)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                                NotificationManagerCompat nManager = NotificationManagerCompat.from(ToBankActivity.this);
                                if (ActivityCompat.checkSelfPermission(ToBankActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                nManager.notify(1, builder.build());
                            } else {
                                Toast.makeText(this, "Insufficient balance for the transaction", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Invalid target bank account details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the case where "BALANCE" column doesn't exist
                        Log.d("no bal", "Username" + user_str);
                    }
                } else {
                    Toast.makeText(this, "No bank account found for the logged-in user", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        }
        else
        {
            Toast.makeText(ToBankActivity.this, "Wrong UPI Pin is entered.", Toast.LENGTH_SHORT).show();
            upiB.setText("");
        }
    }

    private boolean validateInput(String accNumber, String retypeAccNumber, String ifscCode, String accHolderName, String amount) {
        // Implement your validation logic here
        return true; // Return true if input is valid, false otherwise
    }

    private void logTransaction(String loggedInUsername, double balance1, String targetAccNumber, double transactionAmount) {
        // Implement your transaction logging logic here
    }
}
