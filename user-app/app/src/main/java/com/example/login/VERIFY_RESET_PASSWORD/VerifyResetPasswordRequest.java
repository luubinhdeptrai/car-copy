package com.example.login.VERIFY_RESET_PASSWORD;

// package com.example.login.VERIFY_RESET_PASSWORD; // Hoặc một package thích hợp khác

import com.google.gson.annotations.SerializedName;

public class VerifyResetPasswordRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("newpassword")
    private String newPassword;
    @SerializedName("providedCode")
    private String providedCode;

    public VerifyResetPasswordRequest(String email, String newPassword, String providedCode) {
        this.email = email;
        this.newPassword = newPassword;
        this.providedCode = providedCode;
    }

    // Getters (tùy chọn, nhưng tốt cho việc debugging hoặc truy cập dữ liệu đã gửi)
    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getProvidedCode() {
        return providedCode;
    }
}
