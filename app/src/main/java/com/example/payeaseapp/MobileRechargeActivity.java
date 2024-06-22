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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MobileRechargeActivity extends AppCompatActivity {

    ImageButton backM;
    Spinner operatorM, packM;
    MaterialButton paybtnM;
    EditText upiM;
    String operatorStr, upiPin;
    int packPrice;

    DBHandler dbHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);

        upiM = findViewById(R.id.upiM);


        operatorM = findViewById(R.id.operatorM);
        String[] mobileOperator = {"Jio", "Airtel", "V!", "BSNL", "MTNL"};

        List<String> operatorList = new ArrayList<>();
        operatorList.add("Jio");
        operatorList.add("Airtel");
        operatorList.add("VI");
        operatorList.add("BSNL");
        operatorList.add("MTNL");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mobileOperator);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operatorM.setAdapter(adapter);

        operatorM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operatorStr = operatorList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                operatorStr = null;
            }
        });

        packM = findViewById(R.id.packM);
        String[] pack = {"666", "479", "259", "199", "2545"};

        List<Integer> packList = new ArrayList<>();
        packList.add(666);
        packList.add(479);
        packList.add(259);
        packList.add(199);
        packList.add(2545);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pack);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packM.setAdapter(adapter1);

        packM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                packPrice = packList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                packPrice = 0;
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

        paybtnM = findViewById(R.id.paybtnM);
        paybtnM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler = new DBHandler(MobileRechargeActivity.this);
                upiPin = upiM.getText().toString();
                String upi = dbHandler.getUpiPin(MobileRechargeActivity.this);

                if(upiPin.equals(upi)) {
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
                            if (balance1 >= packPrice) {
                                // Perform the transaction
                                balance1 -= packPrice;
                                ContentValues values1 = new ContentValues();
                                values1.put("BALANCE", balance1);
                                db.update("payEaseBank", values1, "BANK_ACC_NAME = ?", new String[]{user_str});

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = dateFormat.format(new Date());
                                ContentValues transactionValues = new ContentValues();
                                transactionValues.put("userName", user_str);
                                transactionValues.put("BALANCE", balance1);
                                transactionValues.put("receiver", operatorStr);
                                transactionValues.put("deduct", packPrice);
                                transactionValues.put("timedate", formattedDate);
                                db.insert("payEaseBTransaction", null, transactionValues);

                                String bal1 = Double.toString(balance1);
                                String packP = Double.toString(packPrice);

                                Intent i1 = new Intent(MobileRechargeActivity.this, SuccessActivity.class);
                                i1.putExtra("Username", operatorStr);
                                i1.putExtra("AmountPaid", packP);
                                i1.putExtra("UpdatedBalance", bal1);
                                startActivity(i1);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MobileRechargeActivity.this, "n001")
                                        .setSmallIcon(R.drawable.payease_splashscreen)
                                        .setContentTitle("Mobile Recharge Payment")
                                        .setContentText("Recharge successfully done to " +operatorStr)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                                NotificationManagerCompat nManager = NotificationManagerCompat.from(MobileRechargeActivity.this);
                                if (ActivityCompat.checkSelfPermission(MobileRechargeActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
                                Toast.makeText(MobileRechargeActivity.this, "Insufficient balance for the transaction", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MobileRechargeActivity.this, "Invalid details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MobileRechargeActivity.this, "No bank account found for the logged-in user", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MobileRechargeActivity.this, "Wrong UPI Pin is entered.", Toast.LENGTH_SHORT).show();
                    upiM.setText("");
                }
            }
        });

        backM = findViewById(R.id.backM);
        backM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MobileRechargeActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });
    }
}