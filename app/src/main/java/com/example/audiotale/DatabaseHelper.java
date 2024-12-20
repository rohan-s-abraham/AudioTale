package com.example.audiotale;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.audiotale.Book;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 6;

    // Table and columns for Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SUBSCRIPTION = "sub";

    // Table and columns for Books
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_BOOK_NAME = "name";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_RELEASE_DATE = "release_date";
    private static final String COLUMN_ABSTRACT = "abstract";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_COVER_PHOTO = "cover_photo";

    // Table and columns for BookRequests
    private static final String TABLE_BOOK_REQUESTS = "book_requests";
    private static final String COLUMN_REQUEST_ID = "request_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_BOOK_NAME_REQUEST = "book_name";
    private static final String COLUMN_AUTHOR_NAME = "author_name";
    private static final String COLUMN_RELEASE_YEAR = "release_year";
    private static final String COLUMN_REQUEST_DATE = "request_date";



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

        // Create BookRequests table
        String CREATE_BOOK_REQUESTS_TABLE = "CREATE TABLE " + TABLE_BOOK_REQUESTS + "("
                + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_BOOK_NAME_REQUEST + " TEXT, "
                + COLUMN_AUTHOR_NAME + " TEXT, "
                + COLUMN_RELEASE_YEAR + " INTEGER, "
                + COLUMN_REQUEST_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_BOOK_REQUESTS_TABLE);

        String CREATE_SUBSCRIBED_USERS_TABLE = "CREATE TABLE subscribed_users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "accepted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "end_date TIMESTAMP NOT NULL, "
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(CREATE_SUBSCRIBED_USERS_TABLE);
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

        // Upgrade to version 5 (Add BookRequests table)
        if (oldVersion < 5) {
            String CREATE_BOOK_REQUESTS_TABLE = "CREATE TABLE " + TABLE_BOOK_REQUESTS + "("
                    + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USER_ID + " INTEGER, "
                    + COLUMN_BOOK_NAME_REQUEST + " TEXT, "
                    + COLUMN_AUTHOR_NAME + " TEXT, "
                    + COLUMN_RELEASE_YEAR + " INTEGER, "
                    + COLUMN_REQUEST_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "+ ")";
            db.execSQL(CREATE_BOOK_REQUESTS_TABLE);
        }

        // Upgrade to version 6 (Add SubscribedUsers table)
        if (oldVersion < 6) {
            // Create the subscribed_users table during upgrade
            String CREATE_SUBSCRIBED_USERS_TABLE = "CREATE TABLE subscribed_users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER NOT NULL, "
                    + "accepted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "end_date TIMESTAMP NOT NULL, "
                    + "FOREIGN KEY(user_id) REFERENCES users(id))";
            db.execSQL(CREATE_SUBSCRIBED_USERS_TABLE);
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

    // For login
    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean userExists = cursor.moveToFirst(); // True if a matching record is found
        cursor.close();
        db.close();
        return userExists;
    }

    // For forget password
    public boolean checkUserExistance(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, email});

        boolean userExists = cursor.moveToFirst(); // True if a matching record is found
        cursor.close();
        db.close();
        return userExists;
    }

    // Method to update the password
    public int updatePassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password);

        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
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

    //returns subscription type when Id is passed
    public Integer getSubscriptionTypeById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "users",                          // Table name
                new String[]{"sub"}, // Column to fetch
                "id=?",                           // WHERE clause
                new String[]{String.valueOf(userId)}, // Arguments for WHERE clause
                null, null, null                  // Group By, Having, Order By (not needed here)
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {  // Ensure the cursor moves to the first row
                    int subscriptionType = cursor.getInt(cursor.getColumnIndexOrThrow("sub"));
                    return subscriptionType;
                } else {
                    Log.d("Debug", "No subscription type found for user ID: " + userId);
                }
            } finally {
                cursor.close();  // Always close the cursor in the finally block
            }
        } else {
            Log.d("Debug", "Cursor is null for user ID: " + userId);
        }

        return null;  // Return null if no subscription type is found
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

                return user;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            } finally {
                cursor.close();
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


    // Method to add a book request
    public long addBookRequest(int userId, String bookName, String authorName, int releaseYear) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BOOK_NAME_REQUEST, bookName);
        values.put(COLUMN_AUTHOR_NAME, authorName);
        values.put(COLUMN_RELEASE_YEAR, releaseYear);

        long result = db.insert(TABLE_BOOK_REQUESTS, null, values);
        db.close();
        return result;
    }

    // Method to get all book requests for a user
    public List<BookRequest> getBookRequestsForUser(int userId) {
        List<BookRequest> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOK_REQUESTS,
                new String[]{COLUMN_REQUEST_ID, COLUMN_BOOK_NAME_REQUEST, COLUMN_AUTHOR_NAME, COLUMN_RELEASE_YEAR, COLUMN_REQUEST_DATE},
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookRequest request = new BookRequest(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NAME_REQUEST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                );
                requests.add(request);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requests;
    }

    public List<BookRequest> getAllBookRequests() {
        List<BookRequest> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Modify query to select user_id as well
        Cursor cursor = db.query("book_requests", null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Fetch the user_id from the cursor
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));  // Assuming 'user_id' column exists in the table

                // Create BookRequest with userId
                BookRequest request = new BookRequest(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NAME_REQUEST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_YEAR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                );

                requests.add(request);
            }
            cursor.close();
        }
        return requests;
    }


    public boolean deleteBookRequestById(int requestId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("book_requests", "request_id = ?", new String[]{String.valueOf(requestId)});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean addSubscribedUser(int userId, int subscriptionType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);

        // Calculate end_date based on subscriptionType
        long currentTime = System.currentTimeMillis();
        long endTime;
        if (subscriptionType == 1) { // Monthly
            endTime = currentTime + (30L * 24 * 60 * 60 * 1000); // 30 days
        } else if (subscriptionType == 2) { // Yearly
            endTime = currentTime + (365L * 24 * 60 * 60 * 1000); // 365 days
        } else {
            return false; // Invalid subscription type
        }

        // Convert endTime to ISO 8601 format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String endDateString = sdf.format(new Date(endTime));

        values.put("end_date", endDateString);

        // Insert into the database
        long result = db.insert("subscribed_users", null, values);
        db.close();
        return result != -1; // Return true if insert was successful
    }

    public SubscriptionDetails getSubscriptionDetails(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("subscribed_users", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        SubscriptionDetails details = null;
        if (cursor != null && cursor.moveToFirst()) {
            // Check if the column index is valid
            int acceptedDateIndex = cursor.getColumnIndex("accepted_date");
            int endDateIndex = cursor.getColumnIndex("end_date");

            if (acceptedDateIndex >= 0 && endDateIndex >= 0) {
                details = new SubscriptionDetails(
                        cursor.getString(acceptedDateIndex),
                        cursor.getString(endDateIndex)
                );
            }
            cursor.close();
        }
        db.close();
        return details;
    }

    public String getSubscriptionEndDate(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT end_date FROM subscribed_users WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        String endDate = null;
        if (cursor != null && cursor.moveToFirst()) {
            // Check if the column index is valid
            int endDateIndex = cursor.getColumnIndex("end_date");
            if (endDateIndex >= 0) {
                endDate = cursor.getString(endDateIndex);
            }
            cursor.close();
        }
        db.close();
        return endDate;
    }


    public boolean deleteSubscription(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("subscribed_users", "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsDeleted > 0;
    }


    // For the Subscribed user activity
    public List<SubscribedUser> getSubscribedUsers() {
        List<SubscribedUser> subscribedUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u.id, u.username, u.email, u.sub, s.accepted_date, s.end_date " +
                "FROM subscribed_users s " +
                "JOIN users u ON s.user_id = u.id " +
                "ORDER BY s.end_date ASC";

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    Integer sub = cursor.getInt(cursor.getColumnIndexOrThrow("sub"));
                    String acceptedDate = cursor.getString(cursor.getColumnIndexOrThrow("accepted_date"));
                    String endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));

                    subscribedUsers.add(new SubscribedUser(id, username, email, sub, acceptedDate, endDate));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving subscribed users: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return subscribedUsers;
    }
}
