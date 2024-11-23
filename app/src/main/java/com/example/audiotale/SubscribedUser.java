package com.example.audiotale;

public class SubscribedUser {
    private String username;
    private String email;
    private String endDate;

    public SubscribedUser(String username, String email, String endDate) {
        this.username = username;
        this.email = email;
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getEndDate() {
        return endDate;
    }
}
