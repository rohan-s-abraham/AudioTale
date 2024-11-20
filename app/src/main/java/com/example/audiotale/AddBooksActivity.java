package com.example.audiotale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBooksActivity extends AppCompatActivity {

    private ImageView ivCoverPhoto;
    private EditText etBookName, etAuthorName, etReleaseDate, etAbstract, etStory;
    private DatabaseHelper dbHelper;
    private byte[] coverPhotoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        ivCoverPhoto = findViewById(R.id.ivCoverPhoto);
        etBookName = findViewById(R.id.etBookName);
        etAuthorName = findViewById(R.id.etAuthorName);
        etReleaseDate = findViewById(R.id.etReleaseDate);
        etAbstract = findViewById(R.id.etAbstract);
        etStory = findViewById(R.id.etStory);
        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnAddBook).setOnClickListener(v -> addBook());
    }

    public void selectCoverPhoto(View view) {
        // Open image picker
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                ivCoverPhoto.setImageBitmap(bitmap);
                coverPhotoData = convertBitmapToByteArray(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
        return stream.toByteArray();
    }

    private void addBook() {
        String name = etBookName.getText().toString().trim();
        String author = etAuthorName.getText().toString().trim();
        String releaseDate = etReleaseDate.getText().toString().trim();
        String bookAbstract = etAbstract.getText().toString().trim();
        String story = etStory.getText().toString().trim();

        if (coverPhotoData != null && !name.isEmpty() && !author.isEmpty() && !releaseDate.isEmpty()
                && !bookAbstract.isEmpty() && !story.isEmpty()) {


            long result = dbHelper.addBook(name, author, releaseDate, bookAbstract, story, coverPhotoData);

            if (result != -1) {
                Toast.makeText(this, "Book added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after adding
            } else {
                Toast.makeText(this, "Failed to add book. Try again!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill all fields and select a cover photo.", Toast.LENGTH_SHORT).show();
        }
    }
}