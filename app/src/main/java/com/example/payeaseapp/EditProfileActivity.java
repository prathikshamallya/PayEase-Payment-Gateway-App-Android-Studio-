package com.example.payeaseapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    private EditText newNameEditText, newPasswordEditText, confirmNewPasswordEditText, newUPIPinEditText, confirmNewUPIPinEditText, emailE;

    private Button updatebtn;
    private DBHandler dbHandler;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{8,}" +                // at least 8 characters
                    "$");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent i = getIntent();
        String old_username = i.getStringExtra("username");

        newPasswordEditText = findViewById(R.id.changePass);
        confirmNewPasswordEditText = findViewById(R.id.changeCPass);
        newUPIPinEditText = findViewById(R.id.changePin);
        confirmNewUPIPinEditText = findViewById(R.id.confrmPin);
        updatebtn = findViewById(R.id.updatebtn);

        dbHandler = new DBHandler(this);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateProfileClick(old_username);
            }
        });
    }

    public void onUpdateProfileClick(String old_user) {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();
        String newUPIPin = newUPIPinEditText.getText().toString().trim();
        String confirmNewUPIPin = confirmNewUPIPinEditText.getText().toString().trim();

        if (!validatePassword(newPassword)) {
            newPasswordEditText.setError("Password too weak");
            newPasswordEditText.setText("");
        } else if (!validateConfrmPassword(newPassword, confirmNewPassword)) {
            confirmNewPasswordEditText.setError("Password does not Match");
            confirmNewPasswordEditText.setText("");
        } else if (!validateConfrmPin(newUPIPin, confirmNewUPIPin)) {
            confirmNewUPIPinEditText.setError("PIN does not Match.");
            confirmNewUPIPinEditText.setText("");
        } else {
            dbHandler.updateUserProfile(old_user, newPassword, confirmNewPassword, newUPIPin);
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(EditProfileActivity.this, ProfilePageActivity.class);
            finish();
            startActivity(i1);
        }
    }
    public boolean validatePassword(String pass1) {
        return PASSWORD_PATTERN.matcher(pass1).matches();
    }

    private boolean validateConfrmPassword(String pass1, String cp1)
    {
        return pass1.equals(cp1);
    }

    private boolean validateConfrmPin(String pin, String cPin)
    {
        return pin.equals(cPin);
    }

}
