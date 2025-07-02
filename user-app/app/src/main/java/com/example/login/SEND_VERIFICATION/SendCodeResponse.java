package com.example.login.SEND_VERIFICATION;

import com.google.gson.annotations.SerializedName;

public class SendCodeResponse {
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