package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingHistoryResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("data")
    private List<BookingHistoryItem> data;

    public boolean isSuccess() {
        return success;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<BookingHistoryItem> getData() {
        return data;
    }
}
