package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CreatePaymentUrlRequest {
    @SerializedName("bookingId")
    private String bookingId;

    public CreatePaymentUrlRequest(String bookingId) {
        this.bookingId = bookingId;
    }
}