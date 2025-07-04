package com.example.login.API;

import com.example.login.LOGIN.LoginRequest;
import com.example.login.LOGIN.LoginResponse;
import com.example.login.MODELS.CanRefundResponse;
import com.example.login.MODELS.CanReviewResponse;
import com.example.login.MODELS.CreateIssueRequest;
import com.example.login.MODELS.CreateReviewRequest;
import com.example.login.MODELS.CreateReviewResponse;
import com.example.login.MODELS.DeleteAccountResponse;
import com.example.login.MODELS.IssueListResponse;
import com.example.login.MODELS.IssueResponse;
import com.example.login.MODELS.LockSeatResponse;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.RefundRequest;
import com.example.login.MODELS.RefundResponse;
import com.example.login.MODELS.TicketSeatListResponse;
import com.example.login.MODELS.TicketSeatResponse; // Import mới
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.TripSearchResponse;
import com.example.login.MODELS.UnlockSeatsRequest;
import com.example.login.MODELS.UpdateIssueRequest;
import com.example.login.MODELS.UpdateProfileRequest;
import com.example.login.MODELS.UpdateReviewRequest;
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
import com.google.gson.annotations.SerializedName;

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


    // === API MỚI CHO CUSTOMER SEARCH TRIPS ===
    @GET("api/trips/search")
    Call<TripSearchResponse> searchTrips(
            @Query("originCity") String originCity,
            @Query("destinationCity") String destinationCity,
            @Query("departureDate") String departureDate // Định dạng YYYY-MM-DD
    );
    // ==========================================

    // === API MỚI/CẬP NHẬT CHO SEAT SELECTION & BOOKING ===
    // API để lấy trạng thái ghế cho một chuyến đi (backend: getTicketsForTrip)
    @GET("api/trips/{tripId}/tickets")
    Call<TicketSeatListResponse> getTicketsForTrip(@Path("tripId") String tripId);

    // API để khóa ghế (backend: lockManySeats)
    @POST("api/bookings/lock-many")
    Call<LockSeatResponse> lockManySeats(@Body LockManySeatsRequest request); // request includes tripId, originStopId, destinationStopId

    // API để xác nhận booking (backend: confirmBooking)
    @POST("api/bookings/confirm")
    Call<ConfirmBookingResponse> confirmBooking(@Body ConfirmBookingRequest request); // request includes originStopId, destinationStopId
    // ====================================================

    // === CÁC API KHÁC (ĐẢM BẢO MODEL TRẢ VỀ ĐÚNG) ===
    @POST("api/bookings/create-payment-url")
    Call<CreatePaymentUrlResponse> createPaymentUrl(@Body CreatePaymentUrlRequest request);

    @GET("api/bookings/{bookingId}/payment-status")
    Call<PaymentStatusResponse> getPaymentStatus(@Path("bookingId") String bookingId);

    @DELETE("api/users/deleteMe")
    Call<DeleteAccountResponse> deleteAccount();

    @GET("api/users/me")
    Call<ProfileResponse> getUserProfile();

    @GET("api/bookings/{bookingId}/refundable")
    Call<CanRefundResponse> isBookingRefundable(@Path("bookingId") String bookingId);

    @POST("api/bookings/refund")
    Call<RefundResponse> refundBooking(@Body RefundRequest request);

    @GET("api/bookings/my-history")
    Call<BookingHistoryResponse> getMyBookingHistory();

    @PATCH("api/users/updateMe")
    Call<ProfileResponse> updateUserProfile(@Body UpdateProfileRequest request);

    @POST("api/bookings/unlock")
    Call<LockSeatResponse> unlockSeats(@Body UnlockSeatsRequest request);

    @GET("api/reviews/can-review/{tripId}")
    Call<CanReviewResponse> canReview(@Path("tripId") String tripId);

    @POST("api/reviews")
    Call<CreateReviewResponse> createReview(@Body CreateReviewRequest request);

    @PATCH("api/reviews/{reviewId}")
    Call<CreateReviewResponse> updateReview(
            @Path("reviewId") String reviewId,
            @Body UpdateReviewRequest request
    );

    @POST("api/issues")
    Call<IssueResponse> createIssue(@Body CreateIssueRequest request);

    @PATCH("api/issues/{id}")
    Call<IssueResponse> updateIssue(
            @Path("id") String issueId,
            @Body UpdateIssueRequest request
    );

    @DELETE("api/issues/{id}")
    Call<IssueResponse> deleteIssue(@Path("id") String issueId);

    @GET("api/issues/my-issues")
    Call<IssueListResponse> getMyIssues();
}
