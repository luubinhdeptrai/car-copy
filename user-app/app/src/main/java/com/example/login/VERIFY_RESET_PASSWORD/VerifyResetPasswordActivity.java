package com.example.login.VERIFY_RESET_PASSWORD;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Corrected from TextView
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.API.ApiClient; // Assuming you have an ApiClient class
import com.example.login.API.ApiService; // Assuming you have an ApiService interface
import com.example.login.LOGIN.LoginActivity; // Assuming your LoginActivity is in this package
import com.example.login.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyResetPasswordActivity extends AppCompatActivity {

    private EditText editTextNewPassword, editTextConfirmPassword, editTextVerificationCode; // Changed to EditText
    private Button btnCompleteReset;
    private String userEmail; // To store the email passed from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password); // Make sure this matches your XML layout file name

        // Ánh xạ các View từ layout
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        btnCompleteReset = findViewById(R.id.btnCompleteReset);

        // Lấy email từ Intent (quan trọng!)
        // Đảm bảo rằng activity gửi mã đã truyền email bằng cách:
        // Intent intent = new Intent(CurrentActivity.this, VerifyResetPasswordActivity.class);
        // intent.putExtra("email", userEmail);
        // startActivity(intent);
        if (getIntent() != null && getIntent().hasExtra("email")) {
            userEmail = getIntent().getStringExtra("email");
        } else {
            // Xử lý trường hợp không nhận được email (ví dụ: hiển thị lỗi và đóng activity)
            Toast.makeText(this, "Lỗi: Không nhận được email người dùng.", Toast.LENGTH_LONG).show();
            finish(); // Đóng activity này
            return;
        }


        // Thiết lập sự kiện click cho nút "Hoàn tất"
        btnCompleteReset.setOnClickListener(v -> {
            resetPassword();
        });
    }

    private void resetPassword() {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String verificationCode = editTextVerificationCode.getText().toString().trim();

        // 1. Kiểm tra các trường rỗng
        if (newPassword.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ tất cả các trường.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp nhau không
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }




        // Nếu tất cả các điều kiện kiểm tra phía client hợp lệ, tiến hành gửi API
        // Khởi tạo ApiService
        ApiService apiService = ApiClient.getNoAuthAPI();

        // Tạo đối tượng request
        VerifyResetPasswordRequest request = new VerifyResetPasswordRequest(userEmail, newPassword, verificationCode);

        // Gọi API
        Call<VerifyResetPasswordResponse> call = apiService.verifyResetPasswordCode(request);
        call.enqueue(new Callback<VerifyResetPasswordResponse>() {
            @Override
            public void onResponse(Call<VerifyResetPasswordResponse> call, Response<VerifyResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VerifyResetPasswordResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(VerifyResetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        // Mở lại LoginActivity sau khi đặt lại mật khẩu thành công
                        Intent intent = new Intent(VerifyResetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa các activity cũ trong stack
                        startActivity(intent);
                        finish(); // Đóng VerifyResetPasswordActivity
                    } else {
                        // API trả về lỗi (success = false)
                        Toast.makeText(VerifyResetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Phản hồi không thành công từ server (ví dụ: 400, 401, 500)
                    String errorMessage = "Lỗi từ server: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(VerifyResetPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyResetPasswordResponse> call, Throwable t) {
                // Lỗi kết nối mạng hoặc lỗi khác
                Toast.makeText(VerifyResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}