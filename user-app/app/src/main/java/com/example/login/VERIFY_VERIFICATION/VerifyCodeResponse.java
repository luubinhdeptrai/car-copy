package com.example.login.VERIFY_VERIFICATION;

import com.google.gson.annotations.SerializedName;

public class VerifyCodeResponse {
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
}