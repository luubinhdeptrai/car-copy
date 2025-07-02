package com.example.login.SEND_RESET_PASSWORD;

// package com.example.login.SEND_RESET_PASSWORD; // Hoặc một package thích hợp khác

import com.google.gson.annotations.SerializedName;

public class SendResetPasswordCodeResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Bạn có thể thêm constructor nếu cần tạo đối tượng này thủ công (ít khi cần cho response)
    // public SendResetPasswordCodeResponse(boolean success, String message) {
    //     this.success = success;
    //     this.message = message;
    // }
}