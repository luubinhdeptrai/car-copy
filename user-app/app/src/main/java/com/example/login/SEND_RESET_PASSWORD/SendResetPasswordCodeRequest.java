package com.example.login.SEND_RESET_PASSWORD;

public class SendResetPasswordCodeRequest {
    private String email;

    public SendResetPasswordCodeRequest(String email) {
        this.email = email;
    }

    // Gson sẽ tự động serialize trường 'email' thành JSON key 'email'
    // Bạn có thể thêm getter nếu cần truy cập giá trị sau khi tạo đối tượng
    public String getEmail() {
        return email;
    }
}