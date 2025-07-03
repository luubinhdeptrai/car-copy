package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class RefundResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;


    public RefundResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
