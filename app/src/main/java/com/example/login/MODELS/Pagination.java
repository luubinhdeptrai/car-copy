package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Pagination implements Serializable {
    @SerializedName("totalCount")
    private int totalCount;
    @SerializedName("totalPages")
    private int totalPages;
    @SerializedName("currentPage")
    private int currentPage;

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}