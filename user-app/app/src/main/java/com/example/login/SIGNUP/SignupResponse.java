package com.example.login.SIGNUP;

import com.example.login.MODELS.User;
import com.google.gson.annotations.SerializedName;

public class SignupResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private User data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
}