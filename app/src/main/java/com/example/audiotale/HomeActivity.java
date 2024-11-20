package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


//import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private EditText searchBar;
    private ListView top10BooksListView, popularBooksListView;
    private Button btnFiction, btnNonFiction, btnRomance, btnScience;
    private ArrayAdapter<String> top10BooksAdapter, popularBooksAdapter;
    private List<String> top10Books, popularBooks;

    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

        // Set up the custom Action Bar
//        View actionBarLayout = LayoutInflater.from(this).inflate(R.layout.action_bar_layout, null);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowCustomEnabled(true);
//            actionBar.setCustomView(actionBarLayout);
//            actionBar.setDisplayShowTitleEnabled(false);  // Hide default title
//        }

        // Set up custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // Hide title if needed
            actionBar.setCustomView(R.layout.action_bar_layout); // Use custom layout
        }



        // Initialize Views
        searchBar = findViewById(R.id.searchBar);
//        top10BooksListView = findViewById(R.id.top10BooksListView);
//        popularBooksListView = findViewById(R.id.popularBooksListView);
//        btnFiction = findViewById(R.id.btnFiction);
//        btnNonFiction = findViewById(R.id.btnNonFiction);
//        btnRomance = findViewById(R.id.btnRomance);
//        btnScience = findViewById(R.id.btnScience);

        // Sample Data
//        top10Books = new ArrayList<>();
//        popularBooks = new ArrayList<>();

//        top10Books.add("The Alchemist");
//        top10Books.add("To Kill a Mockingbird");
//        top10Books.add("1984");
//        top10Books.add("Pride and Prejudice");
//        top10Books.add("The Great Gatsby");
//        top10Books.add("Moby Dick");
//        top10Books.add("War and Peace");
//        top10Books.add("Hamlet");
//        top10Books.add("The Odyssey");
//        top10Books.add("Ulysses");

//        popularBooks.add("Harry Potter");
//        popularBooks.add("The Lord of the Rings");
//        popularBooks.add("Percy Jackson");
//        popularBooks.add("The Hunger Games");
//        popularBooks.add("Twilight");
//        popularBooks.add("Divergent");
//        popularBooks.add("Sherlock Holmes");

        // Set Adapters
//        top10BooksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, top10Books);
//        popularBooksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, popularBooks);

//        top10BooksListView.setAdapter(top10BooksAdapter);
//        popularBooksListView.setAdapter(popularBooksAdapter);

        // Enable Text Filtering for Smooth Search
//        top10BooksListView.setTextFilterEnabled(true);
//        popularBooksListView.setTextFilterEnabled(true);

        // Button Click Listeners for Categories
//        btnFiction.setOnClickListener(view -> openCategoryActivity("Fiction"));
//        btnNonFiction.setOnClickListener(view -> openCategoryActivity("Non-Fiction"));
//        btnRomance.setOnClickListener(view -> openCategoryActivity("Romance"));
//        btnScience.setOnClickListener(view -> openCategoryActivity("Science"));

        // Search Bar Functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                top10BooksAdapter.getFilter().filter(charSequence);
//                popularBooksAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        loadBooks();
    }

    private void loadBooks() {

        List<Book> books = dbHelper.getAllBooks();
        if (books != null) {
            booksAdapter = new BooksAdapter(books, book -> {
                // On book click, open BookDetailsActivity
                Intent intent = new Intent(HomeActivity.this, BookDetailsActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            });
            recyclerView.setAdapter(booksAdapter);
        } else {
            Toast.makeText(this, "No books found", Toast.LENGTH_SHORT).show();
        }
    }


    // Opens a new activity showing books under the selected category
    private void openCategoryActivity(String category) {
        Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
        intent.putExtra("CATEGORY_NAME", category);
        startActivity(intent);
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
            Intent intent = new Intent(this, ProfileActivity.class);
             startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
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