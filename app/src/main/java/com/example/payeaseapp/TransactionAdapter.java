package com.example.payeaseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<TransactionClass> {
    private Context context;
    private ArrayList<TransactionClass> transactions;
    private DBHandler dbHelper;
    public TransactionAdapter(Context context, ArrayList<TransactionClass> transactions, DBHandler dbHelper) {
        super(context, 0, transactions);
        this.context = context;
        this.transactions = transactions;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_list_view, parent, false);
        }

        TransactionClass transactions = getItem(position);

        TextView senderTextView = convertView.findViewById(R.id.senderTextView);
        TextView receiverTextView = convertView.findViewById(R.id.receiverTextView);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        // Set the text values from the Transaction object
        senderTextView.setText("Sender: " +transactions.getSenderId());
        receiverTextView.setText("Receiver: " + transactions.getReceiverId());
        amountTextView.setText(" ₹ " + transactions.getAmount());

        dateTextView.setText("Date: " + transactions.getDate());
        SharedPreferences sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("username_key", "");

        if (userId == transactions.getSenderId()) {
            // User is the sender, set text color to red
            amountTextView.setTextColor(context.getResources().getColor(R.color.red, null));
        } else if (userId == transactions.getReceiverId()) {
            // User is the receiver, set text color to green
            amountTextView.setTextColor(context.getResources().getColor(R.color.green, null));
        }
        return convertView;
    }

}
