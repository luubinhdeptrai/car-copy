package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class DeleteAccountResponse {

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