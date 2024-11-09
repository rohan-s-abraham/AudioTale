// User.java
package com.example.audiotale; // Adjust if using a subpackage, e.g., com.example.audiotale.models

public class User {
    private int id;
    private String name;
    private String email;
    private int subscription;

    // Constructor
    public User(int id, String name, String email, int subscription) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subscription = subscription;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getSubscription() {
        return subscription;
    }
}
