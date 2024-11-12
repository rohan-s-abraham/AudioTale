package com.example.audiotale;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class BookDetailsActivity extends AppCompatActivity {

    private ImageView imageViewCover;
    private EditText editTextName, editTextAuthor, editTextReleaseDate, editTextAbstract, editTextContent;
    private Button buttonUpdate, buttonDelete;
    private DatabaseHelper dbHelper;
    private Book selectedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        imageViewCover = findViewById(R.id.imageViewCover);
        editTextName = findViewById(R.id.editTextName);
        editTextAuthor = findViewById(R.id.editTextAuthor);
        editTextReleaseDate = findViewById(R.id.editTextReleaseDate);
        editTextAbstract = findViewById(R.id.editTextAbstract);
        editTextContent = findViewById(R.id.editTextContent);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);

        dbHelper = new DatabaseHelper(this);

        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        selectedBook = dbHelper.getBookById(bookId);

        if (selectedBook != null) {
            imageViewCover.setImageBitmap(Utils.getImage(selectedBook.getCoverPhoto()));
            editTextName.setText(selectedBook.getName());
            editTextAuthor.setText(selectedBook.getAuthor());
            editTextReleaseDate.setText(selectedBook.getReleaseDate());
            editTextAbstract.setText(selectedBook.getBookAbstract());
            editTextContent.setText(selectedBook.getContent());
        }

        buttonUpdate.setOnClickListener(v -> {
            // Update the book in the database
            selectedBook.setName(editTextName.getText().toString());
            selectedBook.setAuthor(editTextAuthor.getText().toString());
            selectedBook.setReleaseDate(editTextReleaseDate.getText().toString());
            selectedBook.setBookAbstract(editTextAbstract.getText().toString());
            selectedBook.setContent(editTextContent.getText().toString());
            dbHelper.updateBook(selectedBook);
            finish();
        });

        buttonDelete.setOnClickListener(v -> {
            // Delete the book from the database
            dbHelper.deleteBook(selectedBook.getId());
            finish();
        });
    }
}