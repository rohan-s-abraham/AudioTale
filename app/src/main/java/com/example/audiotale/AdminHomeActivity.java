package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class AdminHomeActivity extends AppCompatActivity {

    private Button usermng, subUsers, bookmng, addbook, subreq, bookreq;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.audiotaleblue));
        }

        // Initialize views
        usermng = findViewById(R.id.usermng);
        subUsers = findViewById(R.id.subUsers);
        bookmng = findViewById(R.id.bookmng);
        addbook = findViewById(R.id.addbook);
        subreq = findViewById(R.id.subreq);
        bookreq = findViewById(R.id.bookreq);


        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set up button click listeners
        usermng.setOnClickListener(v -> displayUserList());
        subUsers.setOnClickListener(v -> displaySubList());
        bookmng.setOnClickListener(v -> displayBookList());
        addbook.setOnClickListener(v -> openAddBook());
        subreq.setOnClickListener(v -> displaySubReq());
        bookreq.setOnClickListener(v -> displayBookReq());
    }



    private void displayUserList() {
        // Intent to navigate to User Management Activity
        Intent intent = new Intent(AdminHomeActivity.this, UserManagementActivity.class);
        startActivity(intent);
    }

    private void displaySubList() {
        // Intent to navigate to Subscribed users Activity
        Intent intent = new Intent(AdminHomeActivity.this, SubscribedUsersActivity.class);
        startActivity(intent);

    }

    private void displayBookList() {
        // Intent to navigate to Book Management Activity
        Intent intent = new Intent(AdminHomeActivity.this, ManageBooksActivity.class);
        startActivity(intent);
    }

    private void openAddBook() {
        // Intent to navigate to Add Book Activity
        Intent intent = new Intent(AdminHomeActivity.this, AddBooksActivity.class);
        startActivity(intent);
    }

    private void displaySubReq() {
        // Intent to navigate to Subscription request view Activity
        Intent intent = new Intent(AdminHomeActivity.this, SubscriptionRequests.class);
        startActivity(intent);
    }

    private void displayBookReq() {
        // Intent to navigate to Book request Activity
        Intent intent = new Intent(AdminHomeActivity.this, BookRequestview.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        // Get the "Logout" menu item
        MenuItem logoutItem = menu.findItem(R.id.action_logout);

        // Set a custom font for the menu item
        Typeface customFont = ResourcesCompat.getFont(this, R.font.alkatra);
        SpannableString styledTitle = new SpannableString(logoutItem.getTitle());
        styledTitle.setSpan(new CustomTypefaceSpan("", customFont), 0, styledTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        logoutItem.setTitle(styledTitle);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Perform logout action
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear all session-related data in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clears all saved session data
        editor.apply();

        // Show a message confirming logout
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity and clear activity stack to prevent user from returning with the back button
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
        startActivity(intent);

        finish(); // Finish current activity
    }


}
