package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class LockSeatRequest {
    @SerializedName("ticketId")
    private String ticketId;

    public LockSeatRequest(String ticketId) {
        this.ticketId = ticketId;
    }
}