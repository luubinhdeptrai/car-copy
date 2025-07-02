// File: com/example/login/MODELS/TripSearchResponse.java
package com.example.login.MODELS;

import java.util.List;

public class TripSearchResponse {
    private boolean success;
    private String message;
    private List<Trip> data;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Trip> getData() {
        return data;
    }
}