package com.example.audiotale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionRequests extends AppCompatActivity {

    private ListView requestListView;
    private DatabaseHelper databaseHelper;
    private List<Map<String, String>> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_requests);


        requestListView = findViewById(R.id.requestListView);
        databaseHelper = new DatabaseHelper(this);

        // Load and display subscription requests
        loadRequestList();
    }

    private void loadRequestList() {
        requestList = new ArrayList<>();

        // Fetch all subscription requests from the database
        List<SubReq> subRequests = databaseHelper.getAllSubRequests();

        for (SubReq request : subRequests) {
            Map<String, String> requestMap = new HashMap<>();

            // Fetch username from user ID
            String username = databaseHelper.getUsernameById(request.getId());
            Log.d("Debug", "Request ID: " + request.getId() + ", Username: " + username);

            requestMap.put("name", username != null ? username : "Unknown");

            // Set request type (Monthly or Yearly)
            String reqType = request.getReqType() == 1 ? "Monthly" : "Yearly";
            requestMap.put("type", "Request Type: " + reqType);

            requestList.add(requestMap);
        }

        // Define adapter for ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                requestList,
                R.layout.request_list_item,
                new String[]{"name", "type"},
                new int[]{R.id.requestUserNameTextView, R.id.requestTypeTextView}
        );

        requestListView.setAdapter(adapter);

        // Set item click listeners for accept and delete icons
        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView acceptIcon = view.findViewById(R.id.acceptRequestIcon);
                ImageView deleteIcon = view.findViewById(R.id.deleteRequestIcon);

                acceptIcon.setOnClickListener(v -> showAcceptenceConfirmationDialog(subRequests.get(position), position));
                deleteIcon.setOnClickListener(v -> showDismissConfirmationDialog(subRequests.get(position).getId(), position));
            }
        });
    }

    private void showAcceptenceConfirmationDialog(SubReq request, int position) {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Accept Request")
                .setMessage("Are you sure you want to accept this subscription request?")
                .setPositiveButton("Yes", (dialog, which) -> handleRequestAcceptance(request, position)) // If "Yes" is clicked, delete the user
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // If "No" is clicked, dismiss the dialog
                .show();
    }

    private void handleRequestAcceptance(SubReq request, int position) {
        int newSubStatus = request.getReqType();
        int userId = request.getId(); // Assuming request ID is the user ID.

        // Update user subscription status
        boolean isUpdated = databaseHelper.updateSubscriptionById(userId, newSubStatus);

        if (isUpdated) {
            // Add user to subscribed_users table
            boolean isSubscribed = databaseHelper.addSubscribedUser(userId, newSubStatus);

            if (isSubscribed) {
                // Remove request from list and database
                requestList.remove(position);
                ((SimpleAdapter) requestListView.getAdapter()).notifyDataSetChanged();
                databaseHelper.deleteSubRequestById(userId);
                Toast.makeText(this, "Subscription accepted & saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save subscription details", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to accept request", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDismissConfirmationDialog(int requestId, int position) {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Decline Request")
                .setMessage("Are you sure you want to decline this subscription request?")
                .setPositiveButton("Yes", (dialog, which) -> handleRequestDeletion(requestId, position)) // If "Yes" is clicked, delete the user
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // If "No" is clicked, dismiss the dialog
                .show();
    }

    private void handleRequestDeletion(int requestId, int position) {
        // Delete the request from the database
        boolean isDeleted = databaseHelper.deleteSubRequestById(requestId);

        if (isDeleted) {
            // Remove the request from the list and notify adapter
            requestList.remove(position);
            ((SimpleAdapter) requestListView.getAdapter()).notifyDataSetChanged();
            Toast.makeText(this, "Request declined!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete request", Toast.LENGTH_SHORT).show();
        }
    }
}