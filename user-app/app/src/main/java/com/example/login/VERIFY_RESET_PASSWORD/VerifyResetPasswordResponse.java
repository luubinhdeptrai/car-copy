package com.example.login.VERIFY_RESET_PASSWORD;

// package com.example.login.VERIFY_RESET_PASSWORD; // Hoặc một package thích hợp khác

import com.google.gson.annotations.SerializedName;

public class VerifyResetPasswordResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Constructor (tùy chọn)
    // public VerifyResetPasswordResponse(boolean success, String message) {
    //     this.success = success;
    //     this.message = message;
    // }
}