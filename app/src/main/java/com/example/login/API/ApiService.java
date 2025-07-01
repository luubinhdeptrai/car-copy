package com.example.login.API;

import com.example.login.LOGIN.LoginRequest;
import com.example.login.LOGIN.LoginResponse;
import com.example.login.MODELS.DeleteAccountResponse;
import com.example.login.MODELS.LockSeatResponse;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.TicketSeatListResponse;
import com.example.login.MODELS.TicketSeatResponse; // Import mới
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
import com.example.login.MODELS.ConfirmBookingRequest;
import com.example.login.MODELS.ConfirmBookingResponse;
import com.example.login.MODELS.CreatePaymentUrlRequest;
import com.example.login.MODELS.CreatePaymentUrlResponse;
import com.example.login.MODELS.LockSeatRequest;
import com.example.login.MODELS.LockManySeatsRequest;
import com.example.login.MODELS.PaymentStatusResponse;
import com.example.login.MODELS.BookingHistoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path; // Import mới
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request); // Đang dùng

    @PATCH("api/auth/send-verification-code")
    Call<SendCodeResponse> sendVerificationCode(@Body EmailRequest request);

    @PATCH("api/auth/verify-verification-code")
    Call<VerifyCodeResponse> verifyVerificationCode(@Body VerifyCodeRequest request);

    @PATCH("api/auth/send-reset-password-code")
    Call<SendResetPasswordCodeResponse> sendResetPasswordCode(@Body SendResetPasswordCodeRequest request);

    @PATCH("api/auth/verify-reset-password-code")
    Call<VerifyResetPasswordResponse> verifyResetPasswordCode(@Body VerifyResetPasswordRequest request);

    @GET("api/trips/search")
    Call<TripSearchResponse> searchTrips(
            @Query("originCity") String originCity,
            @Query("destinationCity") String destinationCity,
            @Query("departureDate") String departureDate // Định dạng YYYY-MM-dd
    );

    // Thêm API mới để lấy trạng thái ghế cho một chuyến đi
    @GET("api/trips/{tripId}/tickets")
    Call<TicketSeatListResponse> getTicketsForTrip(@Path("tripId") String tripId);

    // THÊM MỚI: API để xoá tài khoản người dùng hiện tại
    @DELETE("api/users/deleteMe")
    Call<DeleteAccountResponse> deleteAccount();
    @GET("api/users/me")
    Call<ProfileResponse> getUserProfile();


    //Booking & Payment
    @POST("api/booking/lock")
    Call<LockSeatResponse> lockSeat(@Body LockSeatRequest request);

    @POST("api/booking/lock-many")
    Call<LockSeatResponse> lockManySeats(@Body LockManySeatsRequest request);

    @POST("api/booking/confirm")
    Call<ConfirmBookingResponse> confirmBooking(@Body ConfirmBookingRequest request);

    @POST("api/booking/create-payment-url")
    Call<CreatePaymentUrlResponse> createPaymentUrl(@Body CreatePaymentUrlRequest request);

    @GET("api/booking/{bookingId}/payment-status")
    Call<PaymentStatusResponse> getPaymentStatus(@Path("bookingId") String bookingId);

    @GET("api/booking/my-history")
    Call<BookingHistoryResponse> getMyBookingHistory();
}
