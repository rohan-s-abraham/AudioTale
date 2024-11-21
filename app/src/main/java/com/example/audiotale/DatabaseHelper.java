package com.example.audiotale;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.audiotale.Book;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 4; // nerthe 3 arunn, athinu munne 2

    // Table and columns for Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SUBSCRIPTION = "sub";  // New column for subscription

    // Table and columns for Books
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_BOOK_NAME = "name";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_RELEASE_DATE = "release_date";
    private static final String COLUMN_ABSTRACT = "abstract";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_COVER_PHOTO = "cover_photo";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table with 'sub' column
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_SUBSCRIPTION + " INTEGER DEFAULT 0" + ")";  // Default 0 for non-subscribed
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "("
                + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BOOK_NAME + " TEXT, "
                + COLUMN_AUTHOR + " TEXT, "
                + COLUMN_RELEASE_DATE + " TEXT, "
                + COLUMN_ABSTRACT + " TEXT, "
                + COLUMN_CONTENT + " TEXT, "
                + COLUMN_COVER_PHOTO + " BLOB" + ")";
        db.execSQL(CREATE_BOOKS_TABLE);

        // New SubRequests table
        String CREATE_SUBREQUESTS_TABLE = "CREATE TABLE SubRequests ("
                + "id INTEGER PRIMARY KEY, "
                + "reqType INTEGER DEFAULT 0)";
        db.execSQL(CREATE_SUBREQUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_SUBSCRIPTION + " INTEGER DEFAULT 0");
        }

        if (oldVersion < 3) {
            String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "("
                    + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_BOOK_NAME + " TEXT, "
                    + COLUMN_AUTHOR + " TEXT, "
                    + COLUMN_RELEASE_DATE + " TEXT, "
                    + COLUMN_ABSTRACT + " TEXT, "
                    + COLUMN_CONTENT + " TEXT, "
                    + COLUMN_COVER_PHOTO + " BLOB" + ")";
            db.execSQL(CREATE_BOOKS_TABLE);
        }

        if (oldVersion < 4) {
            String CREATE_SUBREQUESTS_TABLE = "CREATE TABLE SubRequests ("
                    + "id INTEGER PRIMARY KEY, "
                    + "reqType INTEGER DEFAULT 0)";
            db.execSQL(CREATE_SUBREQUESTS_TABLE);
        }
    }

    // Method to add a user with default 'sub' value of 0
    public long addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_SUBSCRIPTION, 0);  // Default to 0 at registration

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Method to check if a user is subscribed
    public boolean isUserSubscribed(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_SUBSCRIPTION + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            int subscriptionStatus = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION));
            cursor.close();
            db.close();
            return subscriptionStatus == 1;  // Return true if subscribed
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    // Check if an email already exists
    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean userExists = cursor.moveToFirst(); // True if a matching record is found
        cursor.close();
        db.close();
        return userExists;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id", "username", "email", "sub"}, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sub"))
                );
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    // Fetch username by ID
    public String getUsernameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"username"}, "id=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {  // Ensure the cursor moves to the first row
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                cursor.close();
                return username;
            } else {
                cursor.close();
                Log.d("Debug", "No user found for ID: " + userId);
            }
        } else {
            Log.d("Debug", "Cursor is null for user ID: " + userId);
        }
        return null;  // Return null if no username is found
    }

    public boolean deleteUserById(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("users", "id = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    // Method to update the subscription status
    public int updateSubscriptionStatus(String username, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBSCRIPTION, status);

        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
        return result;
    }

    // Update subscription by user ID
    public boolean updateSubscriptionById(int userId, int subscriptionType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sub", subscriptionType);

        int rowsAffected = db.update("users", values, "id=?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    // Method to update the username
    public int updateUserName(String email, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newName);

        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
    }
    // Method to update the username by email
    public boolean updateUserNameByEmail(String email, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newName);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    public User getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_SUBSCRIPTION},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            int subscriptionIndex = cursor.getColumnIndex(COLUMN_SUBSCRIPTION);

            if (idIndex != -1 && usernameIndex != -1 && emailIndex != -1 && subscriptionIndex != -1) {
                int userId = cursor.getInt(idIndex);
                String username = cursor.getString(usernameIndex);
                String userEmail = cursor.getString(emailIndex);
                int subscriptionStatus = cursor.getInt(subscriptionIndex);

                cursor.close();
                return new User(userId, username, userEmail, subscriptionStatus);
            } else {
                Log.e("DatabaseHelper", "One or more columns not found in users table.");
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;  // Return null if user not found or columns are missing
    }




    // Method to get user details by email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,  // Table name
                null,         // All columns
                COLUMN_EMAIL + "=?",  // WHERE clause to match email
                new String[]{email},  // Arguments for the WHERE clause
                null,         // GROUP BY
                null,         // HAVING
                null          // ORDER BY
        );

        // Ensure cursor is not null and has data
        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Use getColumnIndexOrThrow to ensure columns exist
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));  // User ID column
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));  // Username column
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));  // Email column
                int subscriptionStatus = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION));  // Subscription status column

                // Create User object with retrieved data
                User user = new User(id, name, userEmail, subscriptionStatus);  // Pass all required parameters

                // Return the user object after closing resources
                return user;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();  // Log exception if column is not found
                return null;
            } finally {
                cursor.close();  // Ensure cursor is closed in all cases
                db.close();
            }
        } else {
            // Close the database if no data is found or cursor is null
            if (cursor != null) cursor.close();
            db.close();
            return null;
        }
    }

    // Method to add a book
    public long addBook(String name, String author, String releaseDate, String abstractText, String content, byte[] coverPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_NAME, name);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_RELEASE_DATE, releaseDate);
        values.put(COLUMN_ABSTRACT, abstractText);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_COVER_PHOTO, coverPhoto);

        long result = db.insert(TABLE_BOOKS, null, values);
        db.close();
        return result;
    }

    // Method to get all books
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ABSTRACT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_COVER_PHOTO))
                );
                books.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return books;
    }

    public Book getBookById(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS,
                null, // All columns
                COLUMN_BOOK_ID + "=?", // WHERE clause to match bookId
                new String[]{String.valueOf(bookId)}, // Arguments for WHERE clause
                null, // GROUP BY
                null, // HAVING
                null); // ORDER BY

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming Book has a constructor to initialize fields
            Book book = new Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ABSTRACT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_COVER_PHOTO))
            );
            cursor.close();
            return book;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null; // Return null if book not found
    }


    // Method to update a book by ID
    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_NAME, book.getName());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_RELEASE_DATE, book.getReleaseDate());
        values.put(COLUMN_ABSTRACT, book.getBookAbstract());
        values.put(COLUMN_CONTENT, book.getContent());
        values.put(COLUMN_COVER_PHOTO, book.getCoverPhoto());

        int rowsAffected = db.update(TABLE_BOOKS, values, COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(book.getId())});
        db.close();
        return rowsAffected; // Return the number of rows updated
    }

    // Method to delete a book by ID
    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_BOOKS, COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
        db.close();
        return rowsAffected > 0;
    }


    // Insert or update a subscription request
    public void addOrUpdateSubscriptionRequest(int userId, int reqType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", userId);
        values.put("reqType", reqType);

        long result = db.replace("SubRequests", null, values); // Insert or update
        db.close();
    }

    // Method to fetch all SubRequests
    public List<SubReq> getAllSubRequests() {
        List<SubReq> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("SubRequests", new String[]{"id", "reqType"}, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                SubReq request = new SubReq(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("reqType"))
                );
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return requests;
    }

    // Get subscription request type for a user
    public int getSubscriptionRequestType(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT reqType FROM SubRequests WHERE id=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int reqType = 0;
        if (cursor.moveToFirst()) {
            reqType = cursor.getInt(cursor.getColumnIndexOrThrow("reqType"));
        }
        cursor.close();
        db.close();
        return reqType;
    }

    // Delete subscription request
    public boolean deleteSubRequestById(int requestId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("SubRequests", "id = ?", new String[]{String.valueOf(requestId)});
        return rowsDeleted > 0;
    }

}
