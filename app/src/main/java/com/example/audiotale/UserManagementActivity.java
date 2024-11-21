package com.example.audiotale;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementActivity extends AppCompatActivity {

    private ListView userListView;
    private DatabaseHelper databaseHelper;
    private List<Map<String, String>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        userListView = findViewById(R.id.userListView);
        databaseHelper = new DatabaseHelper(this);

        // Load and display users
        loadUserList();
    }

    private void loadUserList() {
        userList = new ArrayList<>();

        // Fetch all users from the database
        List<User> users = databaseHelper.getAllUsers();

        for (User user : users) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());

            // Convert subscription to "Yes" or "No"
            String subscriptionStatus;
            if (user.getSubscription() > 0) {
                if(user.getSubscription() == 1){
                    subscriptionStatus = "Yes (Monthly)";
                }else{
                    subscriptionStatus = "Yes (Yearly)";
                }

            } else {
                subscriptionStatus = "No";
            }
            userMap.put("subscription", "Subscribed: " + subscriptionStatus);

            userList.add(userMap);
        }

        // Define adapter for ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                userList,
                R.layout.user_list_item,
                new String[]{"name", "email", "subscription"},
                new int[]{R.id.userNameTextView, R.id.userEmailTextView, R.id.userSubscriptionTextView}
        );

        userListView.setAdapter(adapter);

        // Set up delete icon listener
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView deleteIcon = view.findViewById(R.id.deleteUserIcon);
                deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(users.get(position).getId(), position));
            }
        });
    }

    private void showDeleteConfirmationDialog(int userId, int position) {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser(userId, position)) // If "Yes" is clicked, delete the user
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // If "No" is clicked, dismiss the dialog
                .show();
    }

    private void deleteUser(int userId, int position) {
        // Delete the user from the database
        boolean isDeleted = databaseHelper.deleteUserById(userId);

        if (isDeleted) {
            // Remove user from the list and notify adapter
            userList.remove(position);
            ((SimpleAdapter) userListView.getAdapter()).notifyDataSetChanged();
            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }
}
