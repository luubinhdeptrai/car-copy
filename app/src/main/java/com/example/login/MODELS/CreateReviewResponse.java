package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CreateReviewResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Bạn có thể thêm trường 'data' nếu cần xử lý review vừa tạo
    // @SerializedName("data")
    // private ReviewData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}