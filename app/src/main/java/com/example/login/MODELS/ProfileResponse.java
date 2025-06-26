package com.example.login.MODELS;

public class ProfileResponse {
    private boolean success;
    private String message;
    private User data; // Đối tượng User được lồng bên trong

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public User getData() { return data; }
}