package com.example.audiotale;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //This changes the colour of the status bar , bz theres no actionbar is there
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.audiotaleblue));
        }


        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        });

        TextView signUpPrompt = findViewById(R.id.sign_up_prompt);
        signUpPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the SignUpActivity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}