package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CreateReviewRequest {

    @SerializedName("tripId")
    private String tripId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    public CreateReviewRequest(String tripId, int rating, String comment) {
        this.tripId = tripId;
        this.rating = rating;
        this.comment = comment;
    }
}