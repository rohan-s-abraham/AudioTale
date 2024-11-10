package com.example.audiotale;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;

    // Table and columns
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SUBSCRIPTION = "sub";  // New column for subscription

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_SUBSCRIPTION + " INTEGER DEFAULT 0");
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
        // Query with the correct column names
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_SUBSCRIPTION},
                COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve column indexes based on the corrected column names
            int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            int subscriptionIndex = cursor.getColumnIndex(COLUMN_SUBSCRIPTION);

            if (usernameIndex != -1 && emailIndex != -1 && subscriptionIndex != -1) {
                String username = cursor.getString(usernameIndex);
                String userEmail = cursor.getString(emailIndex);
                int subscriptionStatus = cursor.getInt(subscriptionIndex);

                cursor.close();
                // Assuming your User class has a constructor that matches this
                return new User(0,username, userEmail, subscriptionStatus);
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



}
