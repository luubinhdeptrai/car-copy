package com.example.login.VERIFY_VERIFICATION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.VIEW.MainActivity;
import com.example.login.R;
import com.example.login.SEND_VERIFICATION.EmailRequest;
import com.example.login.SEND_VERIFICATION.SendCodeResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifiedActivity extends AppCompatActivity {

    private EditText etVerificationCode;
    private Button btnVerifyCode;
    private TextView tvResendCode, tvInstruction;
    private String userEmail;
    private ApiService apiServiceAuth; // Dùng cho verify và resend code (cần token)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        etVerificationCode = findViewById(R.id.editTextVerificationCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        tvResendCode = findViewById(R.id.textViewResendCode);
        tvInstruction =  findViewById(R.id.tvInstruction);

        // Lấy email từ Intent
        if (getIntent().hasExtra("email")) {
            userEmail = getIntent().getStringExtra("email");
            tvInstruction.setText("Mã xác nhận đã được gửi đến " + userEmail);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy email người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo ApiService CÓ TOKEN cho các request xác thực và gửi lại mã
        apiServiceAuth = ApiClient.getAuthAPI(this);

        btnVerifyCode.setOnClickListener(v -> verifyCode());
        tvResendCode.setOnClickListener(v -> resendCode());
    }

    private void verifyCode() {
        String code = etVerificationCode.getText().toString().trim();

        if (code.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã xác nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        VerifyCodeRequest request = new VerifyCodeRequest(userEmail, code);
        apiServiceAuth.verifyVerificationCode(request).enqueue(new Callback<VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<VerifyCodeResponse> call, Response<VerifyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("Verified", "Xác nhận thành công.");
                    Toast.makeText(VerifiedActivity.this, "Xác nhận tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    // Chuyển đến MainActivity
                    startActivity(new Intent(VerifiedActivity.this, MainActivity.class));
                    finish(); // Đóng VerifiedActivity
                } else {
                    String errorMessage = "Xác nhận thất bại.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage = "Lỗi đọc lỗi từ server khi xác nhận mã.";
                            Log.e("Verified", "Lỗi đọc errorBody khi xác nhận mã: " + e.getMessage());
                        }
                    }
                    Log.e("Verified", "Xác nhận thất bại: " + errorMessage);
                    Toast.makeText(VerifiedActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyCodeResponse> call, Throwable t) {
                Log.e("Verified", "Lỗi kết nối khi xác nhận mã: " + t.getMessage(), t);
                Toast.makeText(VerifiedActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendCode() {
        EmailRequest request = new EmailRequest(userEmail);
        apiServiceAuth.sendVerificationCode(request).enqueue(new Callback<SendCodeResponse>() {
            @Override
            public void onResponse(Call<SendCodeResponse> call, Response<SendCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("Verified", "Gửi lại mã xác nhận thành công.");
                    Toast.makeText(VerifiedActivity.this, "Mã xác nhận đã được gửi lại.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Gửi lại mã thất bại.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage = "Lỗi đọc lỗi từ server khi gửi lại mã.";
                            Log.e("Verified", "Lỗi đọc errorBody khi gửi lại mã: " + e.getMessage());
                        }
                    }
                    Log.e("Verified", "Gửi lại mã thất bại: " + errorMessage);
                    Toast.makeText(VerifiedActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SendCodeResponse> call, Throwable t) {
                Log.e("Verified", "Lỗi kết nối khi gửi lại mã: " + t.getMessage(), t);
                Toast.makeText(VerifiedActivity.this, "Lỗi kết nối khi gửi lại mã.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}