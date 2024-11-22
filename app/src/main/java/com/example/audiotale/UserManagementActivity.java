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
                if (user.getSubscription() == 1) {
                    subscriptionStatus = "Yes (Monthly)";
                } else {
                    subscriptionStatus = "Yes (Yearly)";
                }
                userMap.put("showEye", "true"); // Flag to show the eye icon
            } else {
                subscriptionStatus = "No";
                userMap.put("showEye", "false"); // Flag to hide the eye icon
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
        ) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Set visibility of the eye icon based on subscription status
                ImageView eyeIcon = view.findViewById(R.id.viewUserIcon);
                String showEye = userList.get(position).get("showEye");
                if ("true".equals(showEye)) {
                    eyeIcon.setVisibility(View.VISIBLE);
                    eyeIcon.setOnClickListener(v -> showUserDetailsDialog(users.get(position)));
                } else {
                    eyeIcon.setVisibility(View.GONE);
                }

                // Set up delete icon listener
                ImageView deleteIcon = view.findViewById(R.id.deleteUserIcon);
                deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(users.get(position).getId(), position));

                return view;
            }
        };

        userListView.setAdapter(adapter);
    }

    private void showUserDetailsDialog(User user) {
        // Show user details in a dialog
        SubscriptionDetails subscription = databaseHelper.getSubscriptionDetails(user.getId());

        new AlertDialog.Builder(this)
                .setTitle("User Details")
                .setMessage(
                        "Name: " + user.getName() + "\n" +
                                "Subscription Type: " + (user.getSubscription() == 1 ? "Monthly" : "Yearly") + "\n" +
                                "Starting Date: " + subscription.getAcceptedDate() + "\n" +
                                "Ending Date: " + subscription.getEndDate()
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Revoke Subscription", (dialog, which) -> revokeSubscription(user))
                .show();
    }

    private void revokeSubscription(User user) {
        // Update the user's subscription in the database
        boolean subscriptionRevoked = databaseHelper.updateSubscriptionById(user.getId(), 0);
        boolean subscriptionDeleted = databaseHelper.deleteSubscription(user.getId());

        if (subscriptionRevoked && subscriptionDeleted) {
            loadUserList(); // Reload user list after revocation
            Toast.makeText(this, "Subscription revoked successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to revoke subscription", Toast.LENGTH_SHORT).show();
        }
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
