package com.example.login.API;

import com.example.login.LOGIN.LoginRequest;
import com.example.login.LOGIN.LoginResponse;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.TripSearchResponse;
import com.example.login.SEND_RESET_PASSWORD.SendResetPasswordCodeRequest;
import com.example.login.SEND_RESET_PASSWORD.SendResetPasswordCodeResponse;
import com.example.login.SEND_VERIFICATION.EmailRequest;
import com.example.login.SEND_VERIFICATION.SendCodeResponse;
import com.example.login.SIGNUP.SignupRequest;
import com.example.login.SIGNUP.SignupResponse;
import com.example.login.VERIFY_RESET_PASSWORD.VerifyResetPasswordResponse;
import com.example.login.VERIFY_RESET_PASSWORD.VerifyResetPasswordRequest;
import com.example.login.VERIFY_VERIFICATION.VerifyCodeRequest;
import com.example.login.VERIFY_VERIFICATION.VerifyCodeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request); // Đang dùng

    @PATCH("auth/send-verification-code")
    Call<SendCodeResponse> sendVerificationCode(@Body EmailRequest request);

    @PATCH("auth/verify-verification-code")
    Call<VerifyCodeResponse> verifyVerificationCode(@Body VerifyCodeRequest request);

    @PATCH("auth/send-reset-password-code")
    Call<SendResetPasswordCodeResponse> sendResetPasswordCode(@Body SendResetPasswordCodeRequest request);

    @PATCH("auth/verify-reset-password-code")
    Call<VerifyResetPasswordResponse> verifyResetPasswordCode(@Body VerifyResetPasswordRequest request);

    @GET("api/trips/search")
    Call<TripSearchResponse> searchTrips(
            @Query("originCity") String originCity,
            @Query("destinationCity") String destinationCity,
            @Query("departureDate") String departureDate // Định dạng yyyy-MM-dd
    );
}