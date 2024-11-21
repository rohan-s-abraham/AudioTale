package com.example.audiotale;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class BookRequestview extends AppCompatActivity {

    private ListView requestListView;
    private DatabaseHelper databaseHelper;
    private List<Map<String, String>> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requestview);

        requestListView = findViewById(R.id.requestListView);
        databaseHelper = new DatabaseHelper(this);

        // Load and display book requests
        loadRequestList();
    }

    private void loadRequestList() {
        requestList = new ArrayList<>();

        // Fetch all book requests from the database
        List<BookRequest> bookRequests = databaseHelper.getAllBookRequests();

        for (BookRequest request : bookRequests) {
            Map<String, String> requestMap = new HashMap<>();

            // Fetch username from user ID
            String username = databaseHelper.getUsernameById(request.getUserId());
            requestMap.put("name", username != null ? username : "Unknown");

            // Set request time
            requestMap.put("time", request.getRequestDate());

            requestList.add(requestMap);
        }

        // Define adapter for ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                requestList,
                R.layout.bookrequest_list_item,
                new String[]{"name", "time"},
                new int[]{R.id.requestUserNameTextView, R.id.requestTimeTextView}
        );

        requestListView.setAdapter(adapter);

        // Set up click listeners for list items
        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView eyeIcon = view.findViewById(R.id.eyeIcon);
                ImageView notedButton = view.findViewById(R.id.notedButton);

                // Set up "eye" icon to show details
                eyeIcon.setOnClickListener(v -> showRequestDetails(bookRequests.get(position)));

                // Set up "noted" button to delete request
                notedButton.setOnClickListener(v -> showDeleteConfirmationDialog(bookRequests.get(position).getRequestId(), position));
            }
        });
    }

    private void showRequestDetails(BookRequest request) {
        // Create a dialog to show the full request details
        String username = databaseHelper.getUsernameById(request.getUserId());
        new AlertDialog.Builder(this)
                .setTitle("Request Details")
                .setMessage("Requester: " + username + "\n" +
                        "Book Title: " + request.getBookName() + "\n" +
                        "Book Author: " + request.getAuthorName() + "\n" +
                        "Released Year: " + request.getReleaseYear() + "\n" +
                        "Requested on: " + request.getRequestDate())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDeleteConfirmationDialog(int requestId, int position) {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Remove Request")
                .setMessage("Are you sure you want to mark this request as noted and remove it from the list?")
                .setPositiveButton("Yes", (dialog, which) -> deleteRequest(requestId, position))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteRequest(int requestId, int position) {
        // Delete the request from the database
        boolean isDeleted = databaseHelper.deleteBookRequestById(requestId);

        if (isDeleted) {
            // Remove request from the list and notify adapter
            requestList.remove(position);
            ((SimpleAdapter) requestListView.getAdapter()).notifyDataSetChanged();
            Toast.makeText(this, "Request noted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete request", Toast.LENGTH_SHORT).show();
        }
    }
}
