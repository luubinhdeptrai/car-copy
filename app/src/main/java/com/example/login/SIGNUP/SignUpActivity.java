package com.example.login.SIGNUP;

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
import com.example.login.API.TokenManager;
import com.example.login.LOGIN.LoginActivity;
import com.example.login.LOGIN.LoginRequest;
import com.example.login.LOGIN.LoginResponse;
import com.example.login.R;
import com.example.login.SEND_VERIFICATION.EmailRequest;
import com.example.login.SEND_VERIFICATION.SendCodeResponse;
import com.example.login.VERIFY_VERIFICATION.VerifiedActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText etMail, etPass, etConfirmPass, etPhoneNumber;
    private Button btn_signup;
    private TextView tvLogin; // Thêm TextView cho "Đã có tài khoản? Đăng nhập"
    private ApiService apiServiceNoAuth; // Dùng cho signup và login ban đầu (không cần token)

    String email ;
    String password;
    String confirmPass ;
    String phoneNumber ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etMail = findViewById(R.id.editTextEmail);
        etPass = findViewById(R.id.editTextPassword);
        etConfirmPass = findViewById(R.id.editTextConfirmPassword);
        etPhoneNumber = findViewById(R.id.editTextPhone);
        btn_signup = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.textViewLogin); // Ánh xạ TextView

        // Khởi tạo ApiService KHÔNG TOKEN cho các request đăng ký và đăng nhập ban đầu
        apiServiceNoAuth = ApiClient.getNoAuthAPI();

        btn_signup.setOnClickListener(v -> performSignup());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng SignUpActivity khi chuyển sang Login
        });
    }

    private void performSignup() {
        email = etMail.getText().toString().trim();
        password = etPass.getText().toString().trim();
        confirmPass = etConfirmPass.getText().toString().trim();
        phoneNumber = etPhoneNumber.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        SignupRequest signupRequest = new SignupRequest(email, password, phoneNumber);

        // --- BƯỚC 1: GỌI API ĐĂNG KÝ ---
        apiServiceNoAuth.signup(signupRequest).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("Signup", "Đăng ký thành công: " + response.body().getMessage());
                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công! Đang tự động đăng nhập...", Toast.LENGTH_SHORT).show();

                    // --- BƯỚC 2: TỰ ĐỘNG ĐĂNG NHẬP ĐỂ LẤY TOKEN ---
                    loginAfterSignup(email, password);

                } else {
                    // Xử lý lỗi đăng ký
                    String errorMessage = "Đăng ký thất bại.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage = "Lỗi đọc lỗi từ server.";
                            Log.e("Signup", "Lỗi đọc errorBody: " + e.getMessage());
                        }
                    }
                    Log.e("Signup", "Đăng ký thất bại: " + errorMessage);
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Log.e("Signup", "Lỗi kết nối khi đăng ký: " + t.getMessage(), t);
                Toast.makeText(SignUpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginAfterSignup(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        apiServiceNoAuth.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String token = response.body().getData().getToken();
                    TokenManager.saveToken(SignUpActivity.this, token); // Lưu token

                    Log.d("Signup", "Tự động đăng nhập thành công, token đã được lưu.");
                    Toast.makeText(SignUpActivity.this, "Tự động đăng nhập thành công! Đang gửi mã xác nhận...", Toast.LENGTH_SHORT).show();

                    // --- BƯỚC 3: GỬI MÃ XÁC NHẬN (LÚC NÀY INTERCEPTOR SẼ TỰ ĐÍNH KÈM TOKEN) ---
                    sendVerificationCode(email); // Truyền email vào để sử dụng

                } else {
                    String errorMessage = "Tự động đăng nhập sau khi đăng ký thất bại.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage = "Lỗi đọc lỗi từ server khi tự động đăng nhập.";
                            Log.e("Signup", "Lỗi đọc errorBody khi tự động đăng nhập: " + e.getMessage());
                        }
                    }
                    Log.e("Signup", errorMessage);
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    // Nếu tự động đăng nhập thất bại, chuyển về màn hình đăng nhập thủ công
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Signup", "Lỗi kết nối khi tự động đăng nhập: " + t.getMessage(), t);
                Toast.makeText(SignUpActivity.this, "Lỗi kết nối khi tự động đăng nhập.", Toast.LENGTH_SHORT).show();
                // Nếu lỗi kết nối, chuyển về màn hình đăng nhập thủ công
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void sendVerificationCode(String userEmail) {
        // Tạo ApiService CÓ TOKEN cho request này
        ApiService apiServiceAuth = ApiClient.getAuthAPI(this);

        Log.e("TOKEN", TokenManager.getToken(this));

        apiServiceAuth.sendVerificationCode(new EmailRequest(userEmail)).enqueue(new Callback<SendCodeResponse>() {
            @Override
            public void onResponse(Call<SendCodeResponse> call, Response<SendCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("Signup", "Gửi mã xác nhận thành công.");
                    Toast.makeText(SignUpActivity.this, "Mã xác nhận đã được gửi đến email của bạn.", Toast.LENGTH_LONG).show();

                    // Chuyển sang màn hình xác nhận
                    Intent intent = new Intent(SignUpActivity.this, VerifiedActivity.class);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                    finish(); // Đóng màn hình đăng ký

                } else {
                    String errorMessage = "Gửi mã xác nhận thất bại.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage = "Lỗi đọc lỗi từ server khi gửi mã xác nhận.";
                            Log.e("Signup", "Lỗi đọc errorBody khi gửi mã xác nhận: " + e.getMessage());
                        }
                    }
                    Log.e("Signup", "Gửi mã xác nhận thất bại: " + errorMessage);
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SendCodeResponse> call, Throwable t) {
                Log.e("Signup", "Lỗi kết nối khi gửi mã xác nhận: " + t.getMessage(), t);
                Toast.makeText(SignUpActivity.this, "Lỗi kết nối khi gửi mã xác nhận.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}