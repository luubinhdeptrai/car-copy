package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LockSeatResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Seat> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Seat> getData() {
        return data;
    }
}
