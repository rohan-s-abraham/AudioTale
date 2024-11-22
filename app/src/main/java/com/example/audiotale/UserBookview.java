package com.example.audiotale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener; // Add this import
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class UserBookview extends AppCompatActivity {

    private TextView bookNameTextView, bookAuthorName, bookReleaseDate, bookAbstractTextView, bookStoryTextView, storyBegins;
    private ImageView bookCoverImageView, pauseAudioButton, playAudioButton, startOverButton;
    private Button subscribeButton;
    private LinearLayout audioButtonsLayout;
    private DatabaseHelper dbHelper;
    private Book selectedBook;
    private String userEmail;
    private boolean isSubscribed;

    private TextToSpeech textToSpeech;
    private boolean isTtsInitialized = false;

    private int lastSpokenIndex = 0; // Track the last spoken position
    private String[] storyParts; // Array to hold parts of the story content

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookview);

        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        bookNameTextView = findViewById(R.id.bookNameTextView);
        bookReleaseDate = findViewById(R.id.bookReleaseDate);
        bookAuthorName = findViewById(R.id.bookAuthorName);
        bookAbstractTextView = findViewById(R.id.bookAbstractTextView);
        bookStoryTextView = findViewById(R.id.bookStoryTextView);
        storyBegins = findViewById(R.id.storyBegins);
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        subscribeButton = findViewById(R.id.subscribeButton);
        audioButtonsLayout = findViewById(R.id.audioButtonsLayout);
        playAudioButton = findViewById(R.id.playAudioButton);
        pauseAudioButton = findViewById(R.id.pauseAudioButton);
        startOverButton = findViewById(R.id.startOverButton);

        dbHelper = new DatabaseHelper(this);

        // Initialize Text-to-Speech
        initializeTTS();

        // Retrieve user email from shared preferences
        userEmail = getSharedPreferences("UserSession", MODE_PRIVATE).getString("userEmail", null);

        // Log the retrieved email to ensure it's correct
        Log.d("UserBookview", "User Email: " + userEmail);

        if (userEmail != null) {
            loadUserProfile();
        } else {
            Toast.makeText(this, "No user session found", Toast.LENGTH_SHORT).show();
        }

        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        selectedBook = dbHelper.getBookById(bookId);

        if (selectedBook != null) {
            bookCoverImageView.setImageBitmap(Utils.getImage(selectedBook.getCoverPhoto()));
            bookNameTextView.setText(selectedBook.getName());
            bookAuthorName.setText(selectedBook.getAuthor());
            bookReleaseDate.setText(selectedBook.getReleaseDate());
            bookAbstractTextView.setText(selectedBook.getBookAbstract());
            bookStoryTextView.setText(selectedBook.getContent());
        }

        playAudioButton.setOnClickListener(v -> playBookStory());
        pauseAudioButton.setOnClickListener(v -> pauseTTS());
        startOverButton.setOnClickListener(v -> startOverStory());
    }

    private void initializeTTS() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isTtsInitialized = true;
                }
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playBookStory() {
        if (isTtsInitialized && selectedBook != null) {
            String storyContent = selectedBook.getContent();
            if (storyContent != null && !storyContent.isEmpty()) {
                if (storyParts == null) {
                    // Split the story into sentences or smaller parts
                    storyParts = storyContent.split("\\. "); // Splitting by sentences
                }
                // Speak from the last stopped position
                speakFromIndex(lastSpokenIndex);
            } else {
                Toast.makeText(this, "Story content is empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void speakFromIndex(int index) {
        if (storyParts != null && index < storyParts.length) {
            textToSpeech.speak(storyParts[index], TextToSpeech.QUEUE_FLUSH, null, String.valueOf(index));
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    // No action needed on start
                }

                @Override
                public void onDone(String utteranceId) {
                    // Move to the next part and speak if available
                    lastSpokenIndex++;
                    if (lastSpokenIndex < storyParts.length) {
                        textToSpeech.speak(storyParts[lastSpokenIndex], TextToSpeech.QUEUE_ADD, null, String.valueOf(lastSpokenIndex));
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Toast.makeText(UserBookview.this, "Error in TTS playback", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void pauseTTS() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop(); // Stop playback
        }
    }

    private void startOverStory() {
        lastSpokenIndex = 0; // Reset index to the beginning
        playBookStory(); // Start the story from the beginning
    }


    private void loadUserProfile() {
        // Fetch user details from the database using email
        if (userEmail != null) {
            User user = dbHelper.getUserByEmail(userEmail);
            if (user != null) {
                // Log the retrieved user data for debugging
                Log.d("UserBookview", "User Retrieved: " + user.getName() + ", " + user.getEmail());

                // Check the user's subscription status
                isSubscribed = user.getSubscription() > 0;

                if (isSubscribed) {
                    // Show story if subscribed
                    audioButtonsLayout.setVisibility(View.VISIBLE);
                    storyBegins.setVisibility(View.VISIBLE);
                    bookStoryTextView.setVisibility(View.VISIBLE);
                } else {
                    // Show subscription button if not subscribed
                    subscribeButton.setVisibility(View.VISIBLE);
                    subscribeButton.setOnClickListener(v -> {
                        // Handle subscription logic
                        startActivity(new Intent(this, SubscriptionActivity.class));
                    });
                }
            } else {
                Toast.makeText(this, "Error loading profile: User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User email not found in session", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
