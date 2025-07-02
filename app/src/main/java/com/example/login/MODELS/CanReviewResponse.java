package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CanReviewResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("canReview")
    private boolean canReview;

    public boolean isCanReview() {
        return canReview;
    }
}