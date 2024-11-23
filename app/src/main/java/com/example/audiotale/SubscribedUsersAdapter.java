package com.example.audiotale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscribedUsersAdapter extends RecyclerView.Adapter<SubscribedUsersAdapter.ViewHolder> {
    private List<SubscribedUser> subscribedUsers;
    private OnUserInfoClickListener userInfoClickListener;

    // Callback interface for handling info icon clicks
    public interface OnUserInfoClickListener {
        void onUserInfoClick(SubscribedUser user);
    }

    // Constructor to initialize the user list and the listener
    public SubscribedUsersAdapter(List<SubscribedUser> subscribedUsers, OnUserInfoClickListener userInfoClickListener) {
        this.subscribedUsers = subscribedUsers;
        this.userInfoClickListener = userInfoClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscribed_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubscribedUser user = subscribedUsers.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());
        holder.tvEndDate.setText(user.getEndDate());

        // Set click listener for the info icon
        holder.ivInfoIcon.setOnClickListener(v -> {
            if (userInfoClickListener != null) {
                userInfoClickListener.onUserInfoClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscribedUsers.size();
    }

    public void updateUsers(List<SubscribedUser> updatedUsers) {
        subscribedUsers.clear();
        subscribedUsers.addAll(updatedUsers);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvEndDate;
        ImageView ivInfoIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            ivInfoIcon = itemView.findViewById(R.id.subinfo);
        }
    }
}
