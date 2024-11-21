package com.example.audiotale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

//        Button btnMonthly = findViewById(R.id.btn_monthly);
//        Button btnYearly = findViewById(R.id.btn_yearly);
        ImageView button_back = findViewById(R.id.back_button);
        ImageView monthly_view = findViewById(R.id.monthly_view);
        ImageView yearly_view = findViewById(R.id.yearly_view);

        // Load the animation
        Animation bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);

        // Start animation for each ImageView
        monthly_view.startAnimation(bottomToTop);
        yearly_view.startAnimation(bottomToTop);


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

        monthly_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSubscriptionRequest(1);
            }
        });

        yearly_view.setOnClickListener(new View.OnClickListener() {
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


