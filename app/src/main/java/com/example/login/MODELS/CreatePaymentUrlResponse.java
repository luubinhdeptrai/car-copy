package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CreatePaymentUrlResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private String paymentUrl;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getPaymentUrl() { return paymentUrl; }
}