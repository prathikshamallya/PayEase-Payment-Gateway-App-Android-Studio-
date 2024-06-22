package com.example.payeaseapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ToPhoneActivity extends AppCompatActivity {
    private ImageButton backButton;
    private EditText phoneNumberEditText, amtSend, upi2;
    private Button sendButton;
    public static final String SHARED_PREFS = "login_prefs";
    public static final String USERNAME_KEY = "username_key";
    SharedPreferences sharedPreferences;
    String user_str;

    DBHandler dbHandler;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_phone);

        backButton = findViewById(R.id.backPhone);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        amtSend = findViewById(R.id.amtSend);
        upi2 = findViewById(R.id.upi2);
        sendButton = findViewById(R.id.sendbtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ToPhoneActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetUsernameClick(v);
            }
        });
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        user_str = sharedPreferences.getString(USERNAME_KEY, "");
    }

    public void onGetUsernameClick(View view) {
        dbHandler = new DBHandler(ToPhoneActivity.this);
        String phoneNumber = phoneNumberEditText.getText().toString();
        String amountToSend = amtSend.getText().toString();
        String upiPin = upi2.getText().toString();
        String upi = dbHandler.getUpiPin(ToPhoneActivity.this);

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
            if (!phoneNumber.isEmpty() && !amountToSend.isEmpty() && !upiPin.isEmpty()) {
                DBHandler dbHelper = new DBHandler(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] columns = {"userName"};

                String selection = "phone = ?";
                String[] selectionArgs = {phoneNumber};

                Cursor cursor = db.query("payEaseSignUp", columns, selection, selectionArgs, null, null, null);
                int usernameIndex = cursor.getColumnIndex("userName");
                if (cursor != null && cursor.moveToFirst()) {
                    String username = cursor.getString(usernameIndex);
                    String result = dbHelper.checkTransaction(user_str, amountToSend, username);

                    if (result == null) {
                        // Transaction failed (e.g., insufficient balance)
                        Intent i2 = new Intent(ToPhoneActivity.this, FailureActivity.class);
                        i2.putExtra("AmountPaid", amountToSend);
                        i2.putExtra("Result", "Insufficient Balance");
                        startActivity(i2);
                    } else {
                        // Transaction successful
                        Intent i1 = new Intent(ToPhoneActivity.this, SuccessActivity.class);
                        i1.putExtra("Username", username);
                        i1.putExtra("AmountPaid", amountToSend);
                        i1.putExtra("UpdatedBalance", result);
                        startActivity(i1);
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ToPhoneActivity.this, "n001")
                            .setSmallIcon(R.drawable.payease_splashscreen)
                            .setContentTitle("Phone Payment Done")
                            .setContentText("Payment done successfully to " +username)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                    NotificationManagerCompat nManager = NotificationManagerCompat.from(ToPhoneActivity.this);
                    if (ActivityCompat.checkSelfPermission(ToPhoneActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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

                    cursor.close();
                } else {
                    Intent i2 = new Intent(ToPhoneActivity.this, FailureActivity.class);
                    i2.putExtra("AmountPaid", amountToSend);
                    i2.putExtra("Result", phoneNumber);
                    startActivity(i2);
                }

                db.close();
            } else {
                Toast.makeText(this, "Please enter a phone number and amount to send.", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(ToPhoneActivity.this, "Wrong UPI Pin is entered.", Toast.LENGTH_SHORT).show();
            upi2.setText("");
        }
    }

}


