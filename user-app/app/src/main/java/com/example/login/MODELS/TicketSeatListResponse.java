package com.example.login.MODELS;// File: com/example/login/MODELS/TicketSeatListResponse.java
// SỬA LẠI HOÀN TOÀN

import com.example.login.MODELS.TicketSeatResponse;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TicketSeatListResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<TicketSeatResponse> data;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<TicketSeatResponse> getData() {
        return data;
    }
}