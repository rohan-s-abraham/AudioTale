package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText;
    private TextView emailTextView, avatarTextView, subscriptionStatusTextView;
    private Button updateProfileButton, addSubscriptionButton;
    private DatabaseHelper databaseHelper;
    private String userEmail;
    private boolean isSubscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // Hide title if needed
            actionBar.setCustomView(R.layout.action_bar_layout); // Use custom layout
        }

        // Initialize views
        avatarTextView = findViewById(R.id.avatarTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        addSubscriptionButton = findViewById(R.id.addSubscriptionButton);
        subscriptionStatusTextView = findViewById(R.id.subscriptionStatusTextView);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve user email from shared preferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userEmail = preferences.getString("userEmail", null);

        // Log the retrieved email to ensure it's correct
        Log.d("ProfileActivity", "User Email: " + userEmail);

        if (userEmail != null) {
            loadUserProfile();
        } else {
            Toast.makeText(this, "No user session found", Toast.LENGTH_SHORT).show();
        }

        // Set up update profile button
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        // Set up add subscription button
        addSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(ProfileActivity.this, SubscriptionActivity.class);
                 startActivity(intent);
            }
        });


        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Force Profile to always stay selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() != R.id.nav_profile) {
                // Perform actions for Home or Suggest Book
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Handle Home action
                        // Example: Navigate to home activity
                        finish();
                        break;

                    case R.id.nav_suggest:
                        // Handle Suggest Book action
                        // Example: Navigate to suggest book activity
                        // startActivity(new Intent(this, SuggestBookActivity.class));
                        break;
                }

                // Return false to prevent the selection of other items
                return false;
            }

            // Always allow the Profile icon to remain selected
            return true;
        });

        // Ensure Profile is selected by default
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

    }

    private void loadUserProfile() {
        // Fetch user details from the database using email
        if (userEmail != null) {
            User user = databaseHelper.getUserByEmail(userEmail);
            if (user != null) {
                // Log the retrieved user data for debugging
                Log.d("ProfileActivity", "User Retrieved: " + user.getName() + ", " + user.getEmail());

                nameEditText.setText(user.getName());  // Set the user's name
                emailTextView.setText(user.getEmail()); // Set the user's email
                avatarTextView.setText(user.getName().substring(0, 1).toUpperCase());  // Set the avatar based on the name

                // Check the user's subscription status
                isSubscribed = user.getSubscription() > 0;

                if (isSubscribed) {
                    addSubscriptionButton.setVisibility(View.GONE);
                    subscriptionStatusTextView.setText("You Have Already Subscribed!");
                    subscriptionStatusTextView.setTextColor(Color.GREEN);
                    subscriptionStatusTextView.setVisibility(View.VISIBLE);
                } else {
                    addSubscriptionButton.setVisibility(View.VISIBLE);
                    subscriptionStatusTextView.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Error loading profile: User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User email not found in session", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfile() {
        String updatedName = nameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(updatedName)) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userEmail != null) {
            int isUpdated = databaseHelper.updateUserName(userEmail, updatedName);
            if (isUpdated > 0) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
        }
    }
}
