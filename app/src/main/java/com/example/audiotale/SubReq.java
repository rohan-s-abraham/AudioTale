package com.example.audiotale;

public class SubReq {
    private int id;
    private int reqType;

    // Constructor
    public SubReq(int id, int reqType) {
        this.id = id;
        this.reqType = reqType;

    }

    // Getters
    public int getId() {
        return id;
    }

    public int getReqType() {
        return reqType;
    }


}
