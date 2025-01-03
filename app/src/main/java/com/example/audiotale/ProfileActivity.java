package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
    private int currentUserId;


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
                        showSuggestBookDialog();
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
                    // Fetch subscription details (end date)
                    String subscriptionEndDate = databaseHelper.getSubscriptionEndDate(user.getId());

                    // Format the message for subscription status
                    String subscriptionMessage = "";
                    if(user.getSubscription() == 1) { // 1 for monthly subscription
                        subscriptionMessage = "You Have Subscribed to Monthly Plan!"+"\n \n"+" Expires on: " + subscriptionEndDate;
                    } else { // Yearly subscription
                        subscriptionMessage = "You Have Subscribed to Yearly Plan!"+"\n \n"+" Expires on: " + subscriptionEndDate;
                    }

                    // Update UI
                    addSubscriptionButton.setVisibility(View.GONE);
                    subscriptionStatusTextView.setText(subscriptionMessage);
                    subscriptionStatusTextView.setTextColor(Color.rgb(50, 205, 50)); // Green color
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


    private void showSuggestBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);

        // Inflate and set the custom title
        View customTitle = getLayoutInflater().inflate(R.layout.dialog_title, null);
        builder.setCustomTitle(customTitle); // Set the custom title

        // Inflate and set the main dialog content
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_suggest_book, null);
        builder.setView(dialogView);

        // Get input fields
        EditText bookNameInput = dialogView.findViewById(R.id.book_name_input);
        EditText authorNameInput = dialogView.findViewById(R.id.author_name_input);
        EditText releaseYearInput = dialogView.findViewById(R.id.release_year_input);

        // Set dialog buttons
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Request Admin!", (dialog, which) -> {
            String bookName = bookNameInput.getText().toString();
            String authorName = authorNameInput.getText().toString();
            String releaseYear = releaseYearInput.getText().toString();

            if (bookName.isEmpty() || authorName.isEmpty() || releaseYear.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            addBookRequestToDatabase(bookName, authorName, Integer.parseInt(releaseYear));
        });

        builder.create().show();
    }


    private void addBookRequestToDatabase(String bookName, String authorName, int releaseYear) {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = preferences.getInt("userId", 0);

        long result = databaseHelper.addBookRequest(currentUserId, bookName, authorName, releaseYear);

        if(result != 0){
            Toast.makeText(this, "Request send successfully!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Request failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
