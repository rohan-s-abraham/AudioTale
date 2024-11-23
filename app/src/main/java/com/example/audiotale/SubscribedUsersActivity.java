package com.example.audiotale;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscribedUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SubscribedUsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_users);

        recyclerView = findViewById(R.id.recyclerViewSubscribedUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<SubscribedUser> subscribedUsers = dbHelper.getSubscribedUsers();

        adapter = new SubscribedUsersAdapter(subscribedUsers, this::showSubscriptionDetailsDialog);
        recyclerView.setAdapter(adapter);
    }

    private void showSubscriptionDetailsDialog(SubscribedUser user) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        SubscriptionDetails subscription = dbHelper.getSubscriptionDetails(dbHelper.getUserIdByEmail(user.getUsername()));


        new AlertDialog.Builder(this)
                .setTitle("Subscription Details")
                .setMessage(
                        "Name: " + user.getUsername() + "\n" +
                                "Email: " + user.getEmail() + "\n" +
                                "Subscription Type: " + (user.getSub() == 1 ? "Monthly" : "Yearly") + "\n" +
                                "Starting Date: " + user.getAcceptDate() + "\n" +
                                "Ending Date: " + user.getEndDate()
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Revoke Subscription", (dialog, which) -> {
                    if (dbHelper.updateSubscriptionById(user.getId(), 0) &&
                            dbHelper.deleteSubscription(user.getId())) {
                        Toast.makeText(this, "Subscription revoked successfully!", Toast.LENGTH_SHORT).show();
                        refreshUserList();
                    } else {
                        Toast.makeText(this, "Failed to revoke subscription", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }


    private void refreshUserList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<SubscribedUser> updatedUsers = dbHelper.getSubscribedUsers();
        adapter.updateUsers(updatedUsers);
    }
}
