package com.example.login.LOGIN;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private LoginData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public LoginData getData() {
        return data;
    }

    public static class LoginData {
        @SerializedName("verified")
        private boolean verified;
        @SerializedName("token")
        private String token;

        public boolean isVerified() {
            return verified;
        }

        public String getToken() {
            return token;
        }
    }
}