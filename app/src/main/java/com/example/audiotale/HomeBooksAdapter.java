package com.example.audiotale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class HomeBooksAdapter extends RecyclerView.Adapter<HomeBooksAdapter.HomeBookViewHolder> implements Filterable {

    private List<Book> books;
    private List<Book> filteredBooks;
    private OnHomeBookClickListener listener;

    public interface OnHomeBookClickListener {
        void onBookClick(Book book);
    }

    public HomeBooksAdapter(List<Book> books, OnHomeBookClickListener listener) {
        this.books = books;
        this.filteredBooks = new ArrayList<>(books); // Initialize filtered list
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new HomeBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBookViewHolder holder, int position) {
        Book book = filteredBooks.get(position); // Use filtered list here
        holder.textViewName.setText(book.getName());
        holder.imageViewCover.setImageBitmap(Utils.getImage(book.getCoverPhoto()));

        // Set click listener for each book item
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return filteredBooks.size(); // Return the size of filtered list
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Book> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(books); // Show all books if no search query
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (Book book : books) {
                        if (book.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(book);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredBooks.clear();
                filteredBooks.addAll((List<Book>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class HomeBookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewCover;

        public HomeBookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewBookName);
            imageViewCover = itemView.findViewById(R.id.imageViewBookCover);
        }
    }
}
