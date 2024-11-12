package com.example.audiotale;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

public class ManageBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_books);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);


//        // Example: Clear all rows from the books table when the activity starts
//        clearBooksTable();

        loadBooks();
    }

//    private void clearBooksTable() {
//
//        // Get a writable database
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Delete all rows from the "books" table
//        db.delete("books", null, null);  // Remove all records
//
//        // Optionally, close the database
//        db.close();
//    }

    private void loadBooks() {
        List<Book> books = dbHelper.getAllBooks();
        if (books != null) {
            booksAdapter = new BooksAdapter(books, book -> {
                // On book click, open BookDetailsActivity
                Intent intent = new Intent(ManageBooksActivity.this, BookDetailsActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            });
            recyclerView.setAdapter(booksAdapter);
        } else {
            Toast.makeText(this, "No books found", Toast.LENGTH_SHORT).show();
        }
    }
}