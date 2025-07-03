package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class RefundRequest {
    @SerializedName("bookingId")
    private String bookingId;

    public RefundRequest(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
