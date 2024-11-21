package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;


//import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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


        // Set up custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // Hide title if needed
            actionBar.setCustomView(R.layout.action_bar_layout); // Use custom layout
        }



        // Initialize Views
        searchBar = findViewById(R.id.searchBar);


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

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Force Home to always stay selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() != R.id.nav_home) {
                // Perform actions for Suggest or Profile
                switch (item.getItemId()) {
                    case R.id.nav_suggest:
                        // Handle Suggest Book action
                        // Example: Navigate to suggest book activity
                        // startActivity(new Intent(this, SuggestBookActivity.class));
                        break;

                    case R.id.nav_profile:
                        // Handle Profile action
                        Intent intent = new Intent(this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                }

                // Return false to prevent the selection of other items
                return false;
            }

            // Always allow the Home icon to remain selected
            return true;
        });

        // Ensure Home is selected by default
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


    }



    private void loadBooks() {

        List<Book> books = dbHelper.getAllBooks();
        if (books != null) {
            booksAdapter = new BooksAdapter(books, book -> {
                // On book click, open BookDetailsActivity
                Intent intent = new Intent(HomeActivity.this, UserBookview.class);
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
//        if (id == R.id.action_profile) {
//            // Navigate to Profile Activity
//            Intent intent = new Intent(this, ProfileActivity.class);
//             startActivity(intent);
//            return true;
//        } else
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