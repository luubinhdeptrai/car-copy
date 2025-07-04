package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TicketSeatListResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<Seat> data; // Danh sách các Seat

    public boolean isSuccess() {
        return success;
    }

    public List<Seat> getData() {
        return data;
    }
}