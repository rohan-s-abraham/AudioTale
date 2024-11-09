package com.example.audiotale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private TextView categoryTitle;
    private ListView categoryBooksListView;
    private ArrayAdapter<String> booksAdapter;
    private List<String> booksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

//        // Get the default action bar
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowHomeEnabled(true);   // Show logo on the left
//            actionBar.setLogo(R.drawable.home_icon);          // Replace 'icon' with your logo drawable
//            actionBar.setDisplayUseLogoEnabled(true);    // Enable the logo
//            actionBar.setDisplayShowTitleEnabled(false); // Hide the title text
//        }

        // Set up the toolbar as a custom ActionBar
        // Set up custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // Hide title if needed
            actionBar.setCustomView(R.layout.action_bar_layout); // Use custom layout
        }

        // Initialize Views
        categoryTitle = findViewById(R.id.categoryTitle);
        categoryBooksListView = findViewById(R.id.categoryBooksListView);

        // Get Category Name from Intent
        String category = getIntent().getStringExtra("CATEGORY_NAME");
        categoryTitle.setText(category);

        // Sample Data: Books for the selected category
        booksList = new ArrayList<>();
        if ("Fiction".equals(category)) {
            booksList.add("The Alchemist");
            booksList.add("Pride and Prejudice");
            booksList.add("1984");
        } else if ("Non-Fiction".equals(category)) {
            booksList.add("Sapiens: A Brief History of Humankind");
            booksList.add("Educated");
            booksList.add("Becoming");
        } else if ("Romance".equals(category)) {
            booksList.add("The Notebook");
            booksList.add("Me Before You");
            booksList.add("P.S. I Love You");
        } else if ("Science".equals(category)) {
            booksList.add("A Brief History of Time");
            booksList.add("The Selfish Gene");
            booksList.add("The Gene: An Intimate History");
        }

        // Set Adapter to ListView
        booksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, booksList);
        categoryBooksListView.setAdapter(booksAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the profile menu
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            // Navigate to Profile Activity
            //Intent intent = new Intent(this, ProfileActivity.class);
           // startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            // Perform logout action
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Handle logout functionality here
    }
}
