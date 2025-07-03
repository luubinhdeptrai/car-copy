package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CreateReviewResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ReviewData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ReviewData getData() { return data; }

    public static class ReviewData implements Serializable {
        @SerializedName("_id")
        private String id;

        public String getId() { return id; }
    }
}