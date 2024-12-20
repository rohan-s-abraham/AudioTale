package com.example.audiotale;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        loadBooks();
    }


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

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks(); // Reload book details when the activity resumes
    }
}