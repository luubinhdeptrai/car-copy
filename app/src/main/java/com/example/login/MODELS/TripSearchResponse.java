package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TripSearchResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<TripSearchResult> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<TripSearchResult> getData() {
        return data;
    }
}