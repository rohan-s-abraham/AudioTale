package com.example.audiotale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ForgetPasswordPage1 extends AppCompatActivity {

    private EditText emailEditText;
    private EditText UNEditText;
    private Button nextButton;
    private ImageView  button_back;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_page1);


        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.audiotaleblue));
        }

        emailEditText = findViewById(R.id.emailEditText);
        UNEditText = findViewById(R.id.UNEditText);
        nextButton = findViewById(R.id.ChangeButton);
        button_back = findViewById(R.id.back_button);

        //Back button
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseHelper = new DatabaseHelper(this);

        // Set up login button click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

    }

    private void verifyUser() {
        String email = emailEditText.getText().toString().trim();
        String username = UNEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkUserExistance(username, email)) {
            // Redirect to new password activity
            Intent intent = new Intent(this, ForgetPasswordPage2.class);
            intent.putExtra("email",email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "User does not exist!", Toast.LENGTH_SHORT).show();
        }
    }
}