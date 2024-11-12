package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AdminHomeActivity extends AppCompatActivity {

    private Button usermng, bookmng, addbook;
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
        bookmng = findViewById(R.id.bookmng);
        addbook = findViewById(R.id.addbook);

        // Initialize the database helper (Optional: only if needed here)
        databaseHelper = new DatabaseHelper(this);

        // Set up button click listeners
        usermng.setOnClickListener(v -> displayUserList());
        bookmng.setOnClickListener(v -> displayBookList());
        addbook.setOnClickListener(v -> openAddBook());
    }

    private void displayUserList() {
        // Intent to navigate to User Management Activity
        Intent intent = new Intent(AdminHomeActivity.this, UserManagementActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the profile menu
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // Perform logout action
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Handle logout functionality here
        // Clear only session-related data in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false); // Set isLoggedIn to false
        editor.apply();

        // Show a message confirming logout
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity and clear activity stack to prevent the user from returning with the back button
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish current activity
    }

}
