package com.example.payeaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "login_prefs";
    public static final String USERNAME_KEY = "username_key";
    public static final String EMAIL_KEY = "email_key";
    public static final String PHONE_KEY = "phone_key";
    public static final String PASSWORD_KEY = "password_key";
    SharedPreferences sharedPreferences;
    DBHandler dbHandler;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{8,}" +                // at least 8 characters
                    "$");
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHandler = new DBHandler(SignUpActivity.this);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        EditText username = findViewById(R.id.uName);
        EditText phone = findViewById(R.id.phone);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText c_password = findViewById(R.id.confrmPassword);
        Button login = findViewById(R.id.loginbtn);
        MaterialButton signup = findViewById(R.id.signupbtn);
        ImageButton backbtn =  findViewById(R.id.backbtn);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u1 = username.getText().toString();
                String p1 = phone.getText().toString();
                String pass1 = password.getText().toString();
                String e1 = email.getText().toString();
                String cp1 = c_password.getText().toString();

                if (u1.isEmpty() || p1.isEmpty() || pass1.isEmpty() || e1.isEmpty() || cp1.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter all the required fields.", Toast.LENGTH_SHORT).show();
                } else if (!validateEmail(e1)) {
                    email.setError("Invalid Email");
                    email.setText("");
                } else if (!validatePhone(p1)) {
                    phone.setError("Invalid Phone Number");
                    phone.setText("");
                } else if (!validatePassword(pass1)) {
                    password.setError("Password too weak");
                    password.setText("");
                } else if (!validateConfrmPassword(pass1, cp1)) {
                    c_password.setError("Password does not Match");
                    c_password.setText("");
                } else {
                    // Check if email, phone, password, and c_password are unique
                    Boolean emailUnique = dbHandler.isEmailUnique(e1);
                    Boolean phoneUnique = dbHandler.isPhoneUnique(p1);
                    Boolean passwordUnique = dbHandler.isPasswordUnique(pass1);
                    Boolean cpasswordUnique = dbHandler.isCPasswordUnique(cp1);
                    if (!emailUnique) {
                        email.setError("Email already in use");
                        email.setText("");
                    } else if (!phoneUnique) {
                        phone.setError("Phone number already in use");
                        phone.setText("");
                    } else if (!passwordUnique) {
                        password.setError("Password already in use");
                        password.setText("");
                    } else if (!cpasswordUnique) {
                        c_password.setError("C_Password already in use");
                        c_password.setText("");
                    } else {
                        dbHandler.addNewUser(u1, e1, p1, pass1, cp1);
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString(EMAIL_KEY, e1).apply();
                        sharedPreferences.edit().putString(PHONE_KEY, p1).apply();

                        saveMessage(u1, pass1);

                        Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        username.setText("");
                        email.setText("");
                        phone.setText("");
                        password.setText("");
                        c_password.setText("");

                        Intent intent = new Intent(SignUpActivity.this, AddBankActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }
    private boolean validateEmail(String email1)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email1).matches();
    }

    private boolean validatePhone(String p1)
    {
        String phoneNumberPattern = "^[6-9]\\d{9}$";
        return Pattern.matches(phoneNumberPattern, p1);
    }

    private boolean validatePassword(String pass1) {
        return PASSWORD_PATTERN.matcher(pass1).matches();
    }

    private boolean validateConfrmPassword(String pass1, String cp1)
    {
        return pass1.equals(cp1);
    }

    private void saveMessage(String user, String pass)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, user);
        editor.putString(PASSWORD_KEY, pass);
        editor.apply();
        Toast.makeText(this, "Username and Password saved for future references.", Toast.LENGTH_SHORT).show();
    }
}