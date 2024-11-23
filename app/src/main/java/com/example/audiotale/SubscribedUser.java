package com.example.audiotale;

public class SubscribedUser {
    private Integer id;
    private String username;
    private String email;
    private Integer sub;
    private String acceptDate;
    private String endDate;

    public SubscribedUser(Integer id, String username, String email, Integer sub, String acceptDate, String endDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.sub = sub;
        this.acceptDate = acceptDate;
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getSub() {
        return sub;
    }

    public String getAcceptDate() {
        return acceptDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
