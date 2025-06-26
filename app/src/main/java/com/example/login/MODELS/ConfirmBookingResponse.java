package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class ConfirmBookingResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private BookingData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public BookingData getData() { return data; }

    public static class BookingData {
        @SerializedName("_id")
        private String bookingId;

        public String getBookingId() { return bookingId; }
    }
}