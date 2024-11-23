package com.example.audiotale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
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
    private int currentUserId;

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
                if (booksAdapter != null) {
                    booksAdapter.filter(charSequence.toString()); // Filter books
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set a TouchListener to clear the text when clicking drawableRight
        searchBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[2].getBounds().width())) {
                    searchBar.setText(""); // Clear the text
                    return true;
                }
            }
            return false;
        });

        // Ensure both drawables are set and preserved
        searchBar.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_search, // DrawableLeft
                0,                   // DrawableTop
                R.drawable.ic_clear, // DrawableRight
                0                    // DrawableBottom
        );



        loadBooks();

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Force Home to always stay selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() != R.id.nav_home) {
                // Perform actions for Suggest or Profile
                switch (item.getItemId()) {
                    case R.id.nav_suggest:
                        showSuggestBookDialog();
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

        long result = dbHelper.addBookRequest(currentUserId, bookName, authorName, releaseYear);

        if(result != 0){
            Toast.makeText(this, "Request send successfully!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Request failed!", Toast.LENGTH_SHORT).show();
        }
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