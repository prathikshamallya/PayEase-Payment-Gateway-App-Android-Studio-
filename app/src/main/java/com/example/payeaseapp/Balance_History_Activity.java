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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Balance_History_Activity extends AppCompatActivity{

    TextView balance;
    ListView transactionListView;
    EditText addBalance;
    MaterialButton addMoney;

    private TransactionAdapter transactionAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_history);

        DBHandler dbHandler = new DBHandler(this);

        balance = findViewById(R.id.balance);
        addBalance = findViewById(R.id.addBalance);
        addMoney = findViewById(R.id.addMoney);

        String bal_str = dbHandler.getBalance(this);
        balance.setText("Rs. " +bal_str+ "/-");

        ArrayList<TransactionClass> transactionHistory = dbHandler.getTransactionHistory(this);

        transactionListView = findViewById(R.id.TranList);

        ArrayList<TransactionClass> transactionList = dbHandler.getTransactionHistory(this);
        transactionAdapter = new TransactionAdapter(this, transactionList,dbHandler);
        transactionListView.setAdapter(transactionAdapter);

        ImageButton backbalancebtn = (ImageButton) findViewById(R.id.backbalancebtn);
        backbalancebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Balance_History_Activity.this, HomeActivity.class);
                startActivity(i);
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

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addBal = addBalance.getText().toString();
                double addBal1 = Double.parseDouble(addBal);
                SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
                String user_str = sharedPreferences.getString("username_key", "");

                SQLiteDatabase db = dbHandler.getReadableDatabase();

                String[] columns = {"BALANCE"};
                String selection = "BANK_ACC_NAME = ?";
                String[] selectionArgs = {user_str};
                Cursor cursor = db.query("payEaseBank", columns, selection, selectionArgs, null, null, null);
                if(cursor.moveToFirst()) {
                    int balance1index = cursor.getColumnIndex("BALANCE");
                    double balance1 = Double.parseDouble(cursor.getString(balance1index));
                    balance1 += addBal1;
                    ContentValues values1 = new ContentValues();
                    values1.put("BALANCE", balance1);
                    db.update("payEaseBank", values1, "BANK_ACC_NAME = ?", new String[]{user_str});
                    String bal1 = Double.toString(balance1);
                    balance.setText("Rs. " + bal1 + "/-");
                }
                addBalance.setText("");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Balance_History_Activity.this, "n001")
                        .setSmallIcon(R.drawable.payease_splashscreen)
                        .setContentTitle("Balance Added")
                        .setContentText("Successfullly added money to Bank Balance.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                NotificationManagerCompat nManager = NotificationManagerCompat.from(Balance_History_Activity.this);
                if (ActivityCompat.checkSelfPermission(Balance_History_Activity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
            }
        });
    }
}