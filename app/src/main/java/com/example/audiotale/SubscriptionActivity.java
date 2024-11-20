package com.example.audiotale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SubscriptionActivity extends AppCompatActivity {


    private DatabaseHelper databaseHelper;
    private int currentUserId; // Replace with the actual logged-in user ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.audiotaleblue));
        }


        databaseHelper = new DatabaseHelper(this);

        Button btnMonthly = findViewById(R.id.btn_monthly);
        Button btnYearly = findViewById(R.id.btn_yearly);
        ImageView button_back = findViewById(R.id.back_button);

        //Back button
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Retrieve user email from shared preferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = preferences.getInt("userId", 0);

        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSubscriptionRequest(1);
            }
        });

        btnYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSubscriptionRequest(2);
            }
        });
    }

    private void sendSubscriptionRequest(int reqType) {
        databaseHelper.addOrUpdateSubscriptionRequest(currentUserId, reqType);
        Toast.makeText(this, reqType == 1 ? "Monthly subscription requested!" : "Yearly subscription requested!", Toast.LENGTH_SHORT).show();
    }
}


