package com.example.audiotale;

public class BookRequest {
    private int requestId;
    private String bookName;
    private String authorName;
    private int releaseYear;
    private String requestDate;
    private int userId;  // Added userId

    public BookRequest(int requestId, String bookName, String authorName, int releaseYear, String requestDate, int userId) {
        this.requestId = requestId;
        this.bookName = bookName;
        this.authorName = authorName;
        this.releaseYear = releaseYear;
        this.requestDate = requestDate;
        this.userId = userId;  // Set userId in constructor
    }

    // Getters and setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public int getUserId() { return userId; }  // Getter for userId
    public void setUserId(int userId) { this.userId = userId; }  // Setter for userId
}
