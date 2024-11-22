package com.example.audiotale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<Book> books; // Original list of books
    private List<Book> filteredBooks; // List used for displaying filtered results
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BooksAdapter(List<Book> books, OnBookClickListener listener) {
        this.books = books;
        this.filteredBooks = new ArrayList<>(books); // Initialize with all books
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = filteredBooks.get(position); // Use filtered list
        holder.textViewName.setText(book.getName());
        holder.imageViewCover.setImageBitmap(Utils.getImage(book.getCoverPhoto()));
        holder.bookabstract.setText(book.getBookAbstract());

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return filteredBooks.size(); // Use filtered list
    }

    // Method to filter books based on search query
    public void filter(String query) {
        filteredBooks.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredBooks.addAll(books); // Show all books if query is empty
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Book book : books) {
                if (book.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredBooks.add(book);
                }
            }
        }
        notifyDataSetChanged(); // Update the RecyclerView
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewCover;
        TextView bookabstract;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewBookName);
            imageViewCover = itemView.findViewById(R.id.imageViewBookCover);
            bookabstract = itemView.findViewById(R.id.bookabstract);
        }
    }
}
