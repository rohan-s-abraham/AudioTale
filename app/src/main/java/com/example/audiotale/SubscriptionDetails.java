package com.example.audiotale;

public class SubscriptionDetails {
    private String acceptedDate;
    private String endDate;

    public SubscriptionDetails(String acceptedDate, String endDate) {
        this.acceptedDate = acceptedDate;
        this.endDate = endDate;
    }

    public String getAcceptedDate() {
        return acceptedDate;
    }

    public String getEndDate() {
        return endDate;
    }
}

