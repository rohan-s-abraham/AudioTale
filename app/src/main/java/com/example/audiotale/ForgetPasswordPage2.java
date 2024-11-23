package com.example.audiotale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ForgetPasswordPage2 extends AppCompatActivity {

    private EditText p1EditText, p2EditText;
    private Button changeButton;
    private ImageView  button_back;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_page2);

        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.audiotaleblue));
        }

        p1EditText = findViewById(R.id.p1EditText);
        p2EditText = findViewById(R.id.p2EditText);
        changeButton = findViewById(R.id.ChangeBtn);
        button_back = findViewById(R.id.back_button);

        //Back button
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseHelper = new DatabaseHelper(this);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b != null){
            String username = b.getString("username");
            String email = b.getString("email");
        }

        // Set up login button click listener
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewPassword();
            }
        });



    }

    private void getNewPassword() {

        String email = getIntent().getStringExtra("email");

        String password = p1EditText.getText().toString().trim();
        String confirmpassword = p2EditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmpassword)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmpassword)) {
            Toast.makeText(this, "Password doesn't match !", Toast.LENGTH_SHORT).show();
            return;
        }


        // Add user to the database with default subscription status of 0
        long result = databaseHelper.updatePassword(email, password);

        if (result != -1) {
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

            // Redirect to LoginActivity after successful registration
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Updation failed. Please try again.", Toast.LENGTH_SHORT).show();
        }

    }
}