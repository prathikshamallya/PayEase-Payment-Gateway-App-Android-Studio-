package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToContactsActivity extends AppCompatActivity {

    DBHandler dbHandler;

    ListView cList;

    EditText amtSendC, upi2C;

    MaterialButton sendbtnC;

    String amount, upiPin, checkUpi, contactStr;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_contacts);

        cList = findViewById(R.id.cList);
        amtSendC = findViewById(R.id.amtSendC);
        upi2C = findViewById(R.id.upi2C);
        sendbtnC = findViewById(R.id.sendbtnC);

        ListView listView = findViewById(R.id.cList);
        List<String> list = new ArrayList<>();
        list.add("Megha");
        list.add("Divya");
        list.add("Nireeksha");
        list.add("Jyothika");
        list.add("Tanushree");

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
        cList.setAdapter(arrayAdapter);

        cList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactStr = list.get(position);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PayEaseApp";
            String desc = "Payment App";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("n001", name, imp);
            channel.setDescription(desc);
            NotificationManager nManager = getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(channel);
        }

        sendbtnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler = new DBHandler(ToContactsActivity.this);
                upiPin = upi2C.getText().toString();
                checkUpi = dbHandler.getUpiPin(ToContactsActivity.this);
                amount = amtSendC.getText().toString();
                if(upiPin.equals(checkUpi))
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
                    String user_str = sharedPreferences.getString("username_key", "");
                    SQLiteDatabase db = dbHandler.getReadableDatabase();

                    String[] columns = {"BALANCE"};
                    String selection = "BANK_ACC_NAME = ?";
                    String[] selectionArgs = {user_str};
                    Cursor cursor = db.query("payEaseBank", columns, selection, selectionArgs, null, null, null);

                    if (cursor.moveToFirst()) {
                        int balance1index = cursor.getColumnIndex("BALANCE");

                        if (balance1index >= 0) {
                            double balance1 = Double.parseDouble(cursor.getString(balance1index));
                            double transactionAmount = Double.parseDouble(amount);
                            if (balance1 >= transactionAmount) {
                                // Perform the transaction
                                balance1 -= transactionAmount;
                                ContentValues values1 = new ContentValues();
                                values1.put("BALANCE", balance1);
                                db.update("payEaseBank", values1, "BANK_ACC_NAME = ?", new String[]{user_str});

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = dateFormat.format(new Date());
                                ContentValues transactionValues = new ContentValues();
                                transactionValues.put("userName", user_str);
                                transactionValues.put("BALANCE", balance1);
                                transactionValues.put("receiver", contactStr);
                                transactionValues.put("deduct", amount);
                                transactionValues.put("timedate", formattedDate);
                                db.insert("payEaseBTransaction", null, transactionValues);

                                String bal1 = Double.toString(balance1);

                                Intent i1 = new Intent(ToContactsActivity.this, SuccessActivity.class);
                                i1.putExtra("Username", contactStr);
                                i1.putExtra("AmountPaid", amount);
                                i1.putExtra("UpdatedBalance", bal1);
                                startActivity(i1);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(ToContactsActivity.this, "n001")
                                        .setSmallIcon(R.drawable.payease_splashscreen)
                                        .setContentTitle("Contact Payment Done")
                                        .setContentText("Payment done successfully to " +contactStr)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                                NotificationManagerCompat nManager = NotificationManagerCompat.from(ToContactsActivity.this);
                                if (ActivityCompat.checkSelfPermission(ToContactsActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
                                Toast.makeText(ToContactsActivity.this, "Insufficient balance for the transaction", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ToContactsActivity.this, "Invalid details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ToContactsActivity.this, "No bank account found for " + user_str, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    upi2C.setText("");
                    Toast.makeText(ToContactsActivity.this, "Wrong UPI Pin entered.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton backContact = (ImageButton) findViewById(R.id.backContact);
        backContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ToContactsActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}