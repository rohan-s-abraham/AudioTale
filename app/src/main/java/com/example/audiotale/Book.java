package com.example.audiotale;

public class Book {
    private int id;
    private String name;
    private String author;
    private String releaseDate;
    private String bookAbstract;
    private String content;
    private byte[] coverPhoto;

    // Constructor
    public Book(int id, String name, String author, String releaseDate, String bookAbstract, String content, byte[] coverPhoto) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.releaseDate = releaseDate;
        this.bookAbstract = bookAbstract;
        this.content = content;
        this.coverPhoto = coverPhoto;
    }

    // Getters and Setters for each field

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBookAbstract() {
        return bookAbstract;
    }

    public void setBookAbstract(String bookAbstract) {
        this.bookAbstract = bookAbstract;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(byte[] coverPhoto) {
        this.coverPhoto = coverPhoto;
    }
}
