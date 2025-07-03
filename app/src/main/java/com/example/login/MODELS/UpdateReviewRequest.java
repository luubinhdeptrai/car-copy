package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class UpdateReviewRequest {

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    public UpdateReviewRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}