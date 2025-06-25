package com.example.login.MODELS;// File: com/example/login/MODELS/TicketSeatResponse.java
// SỬA LẠI HOÀN TOÀN

import com.google.gson.annotations.SerializedName;

public class TicketSeatResponse {

    @SerializedName("_id")
    private String id;

    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status; // Trạng thái từ backend: "available", "booked", "locked", ...

    // Getters
    public String getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}