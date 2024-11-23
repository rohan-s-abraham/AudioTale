package com.example.audiotale;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscribedUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SubscribedUsersAdapter adapter;
    private ImageView subinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_users);

//        subinfo=findViewById(R.id.subinfo);

        recyclerView = findViewById(R.id.recyclerViewSubscribedUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<SubscribedUser> subscribedUsers = dbHelper.getSubscribedUsers();

        adapter = new SubscribedUsersAdapter(subscribedUsers);
        recyclerView.setAdapter(adapter);
    }
}
