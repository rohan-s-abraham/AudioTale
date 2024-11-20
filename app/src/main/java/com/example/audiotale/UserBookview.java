package com.example.audiotale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserBookview extends AppCompatActivity {


    private TextView bookNameTextView,bookAuthorName,bookReleaseDate, bookAbstractTextView, bookStoryTextView;
    private ImageView bookCoverImageView;
    private Button subscribeButton;
    private DatabaseHelper dbHelper;
    private Book selectedBook;
    private String userEmail;
    private boolean isSubscribed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookview);

        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        bookNameTextView = findViewById(R.id.bookNameTextView);
        bookReleaseDate = findViewById(R.id.bookReleaseDate);
        bookAuthorName = findViewById(R.id.bookAuthorName);
        bookAbstractTextView = findViewById(R.id.bookAbstractTextView);
        bookStoryTextView = findViewById(R.id.bookStoryTextView);
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        subscribeButton = findViewById(R.id.subscribeButton);

        dbHelper = new DatabaseHelper(this);

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


        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        selectedBook = dbHelper.getBookById(bookId);

        if (selectedBook != null) {
            bookCoverImageView.setImageBitmap(Utils.getImage(selectedBook.getCoverPhoto()));
            bookNameTextView.setText(selectedBook.getName());
            bookAuthorName.setText(selectedBook.getAuthor());
            bookReleaseDate.setText(selectedBook.getReleaseDate());
            bookAbstractTextView.setText(selectedBook.getBookAbstract());
            bookStoryTextView.setText(selectedBook.getContent());
        }



    }

    private void loadUserProfile() {
        // Fetch user details from the database using email
        if (userEmail != null) {
            User user = dbHelper.getUserByEmail(userEmail);
            if (user != null) {
                // Log the retrieved user data for debugging
                Log.d("ProfileActivity", "User Retrieved: " + user.getName() + ", " + user.getEmail());

                // Check the user's subscription status
                isSubscribed = user.getSubscription() == 1;

                if (isSubscribed) {
                    // Show story if subscribed
                    bookStoryTextView.setVisibility(View.VISIBLE);
//                    bookStoryTextView.setText(bookStory);
                } else {
                    // Show subscription button if not subscribed
                    subscribeButton.setVisibility(View.VISIBLE);
                    subscribeButton.setOnClickListener(v -> {
                        // Handle subscription logic
                        Intent intent = new Intent(this, SubscriptionActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "Subscription process...", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(this, "Error loading profile: User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User email not found in session", Toast.LENGTH_SHORT).show();
        }
    }
}