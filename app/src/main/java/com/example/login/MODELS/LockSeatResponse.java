package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class LockSeatResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Seat data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Seat getData() {
        return data;
    }
}