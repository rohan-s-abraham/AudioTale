package com.example.audiotale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscribedUsersAdapter extends RecyclerView.Adapter<SubscribedUsersAdapter.ViewHolder> {
    private List<SubscribedUser> subscribedUsers;

    public SubscribedUsersAdapter(List<SubscribedUser> subscribedUsers) {
        this.subscribedUsers = subscribedUsers;
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
    }

    @Override
    public int getItemCount() {
        return subscribedUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvEndDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
        }
    }
}
