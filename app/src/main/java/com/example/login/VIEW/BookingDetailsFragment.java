package com.example.login.VIEW;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.BookingHistoryItem;
import com.example.login.MODELS.CanRefundResponse;
import com.example.login.MODELS.CanReviewResponse;
import com.example.login.MODELS.CreatePaymentUrlRequest;
import com.example.login.MODELS.CreatePaymentUrlResponse;
import com.example.login.MODELS.CreateReviewRequest;
import com.example.login.MODELS.CreateReviewResponse;
import com.example.login.MODELS.RefundRequest;
import com.example.login.MODELS.RefundResponse;
import com.example.login.MODELS.UpdateReviewRequest;
import com.example.login.R;
import com.google.android.material.textfield.TextInputEditText;
import com.vnpay.authentication.VNP_AuthenticationActivity;
import com.vnpay.authentication.VNP_SdkCompletedCallback;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsFragment extends Fragment {

    private BookingHistoryItem booking;
    private ApiService apiService;
    private String existingReviewId = null;

    private static final String PREFS_NAME = "BookingDetailsPrefs";
    private static final String KEY_DRAFT_RATING_PREFIX = "draft_review_rating_";
    private static final String KEY_DRAFT_COMMENT_PREFIX = "draft_review_comment_";
    private static final String KEY_SAVED_REVIEW_RATING_PREFIX = "saved_review_rating_";
    private static final String KEY_SAVED_REVIEW_COMMENT_PREFIX = "saved_review_comment_";
    private static final String KEY_SAVED_REVIEW_ID_PREFIX = "saved_review_id_";
    private static final String KEY_REVIEW_POSTED_PREFIX = "review_posted_";

    private Toolbar toolbar;
    private TextView providerNameText, bookingIdText, statusText, paymentMethodText,
            totalPriceText, createdAtText, routeText, departureTimeText,
            userNameText, userEmailText, userPhoneText;
    private RecyclerView ticketsRecyclerView;
    private CardView reviewSection;
    private RatingBar ratingBar;
    private TextInputEditText commentEditText;
    private Button submitReviewButton;

    private Button paymentButton;

    private Button refundButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(getContext());
        if (getArguments() != null) {
            booking = (BookingHistoryItem) getArguments().getSerializable("BOOKING_DETAILS");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (booking == null) {
            Toast.makeText(getContext(), "Không thể tải chi tiết đặt vé.", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }
        initViews(view);
        setupToolbar();
        populateData();
        checkCanReview();
        setupPaymentButton();
        setupRefundButton();
        submitReviewButton.setOnClickListener(v -> handleReviewSubmission());
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_booking_details);
        providerNameText = view.findViewById(R.id.provider_name_text);
        bookingIdText = view.findViewById(R.id.booking_id_text);
        statusText = view.findViewById(R.id.status_text);
        paymentMethodText = view.findViewById(R.id.payment_method_text);
        totalPriceText = view.findViewById(R.id.total_price_text);
        createdAtText = view.findViewById(R.id.created_at_text);
        routeText = view.findViewById(R.id.route_text);
        departureTimeText = view.findViewById(R.id.departure_time_text);
        userNameText = view.findViewById(R.id.user_name_text);
        userEmailText = view.findViewById(R.id.user_email_text);
        userPhoneText = view.findViewById(R.id.user_phone_text);
        ticketsRecyclerView = view.findViewById(R.id.tickets_recycler_view);
        reviewSection = view.findViewById(R.id.review_section);
        ratingBar = view.findViewById(R.id.rating_bar);
        commentEditText = view.findViewById(R.id.comment_edit_text);
        submitReviewButton = view.findViewById(R.id.submit_review_button);
        paymentButton = view.findViewById(R.id.payment_button);
        refundButton = view.findViewById(R.id.refund_button);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        toolbar.setTitle("Chi tiết đặt vé");
    }

    private void setupRefundButton() {
        refundButton.setVisibility(View.GONE);
        //only show refund button if payment is completed, approval is confirmed, and trip departure time - current time > 12 hours
        apiService.isBookingRefundable(booking.getId()).enqueue(new Callback<CanRefundResponse>() {
            @Override
            public void onResponse(@NonNull Call<CanRefundResponse> call, @NonNull Response<CanRefundResponse> response) {
                boolean refundable = false;
                if (response.isSuccessful() && response.body() != null) {
                    refundable= response.body().isCanRefund();
                }


                if(refundable) {
                    refundButton.setVisibility(View.VISIBLE);
                    refundButton.setOnClickListener(v -> {
                        apiService.refundBooking( new RefundRequest(booking.getId()))
                                .enqueue(new Callback<RefundResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<com.example.login.MODELS.RefundResponse> call, @NonNull Response<com.example.login.MODELS.RefundResponse> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                            Toast.makeText(getContext(), "Refund request successful!", Toast.LENGTH_SHORT).show();
                                            refundButton.setVisibility(View.GONE);
                                            // show success screen

                                        } else {
                                            Toast.makeText(getContext(), "Refund failed: " + (response.body() != null ? response.body().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<com.example.login.MODELS.RefundResponse> call, @NonNull Throwable t) {
                                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                } else {
                    refundButton.setVisibility(View.GONE);
                }
                // You can use the 'refundable' boolean here as needed
            }

            @Override
            public void onFailure(@NonNull Call<CanRefundResponse> call, @NonNull Throwable t) {
                // Handle failure if needed
            }
        });




    }
    private void setupPaymentButton() {
        paymentButton.setVisibility(View.GONE);
        if (Objects.equals(booking.getPaymentStatus(), "pending") && Objects.equals(booking.getPaymentMethod(), "bank_transfer")) {
            paymentButton.setVisibility(View.VISIBLE);
            paymentButton.setOnClickListener(v -> {
                createPaymentUrl(booking.getId());
                Toast.makeText(getContext(), "Chức năng thanh toán sẽ được cập nhật sau.", Toast.LENGTH_SHORT).show();
            });
        } else {
            paymentButton.setVisibility(View.GONE);
        }
    }
    private void checkCanReview() {
        reviewSection.setVisibility(View.GONE);
        if (booking.getTripInfo() == null || booking.getTripInfo().getId() == null) return;

        apiService.canReview(booking.getTripInfo().getId()).enqueue(new Callback<CanReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<CanReviewResponse> call, @NonNull Response<CanReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isCanReview()) {
                    reviewSection.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CanReviewResponse> call, @NonNull Throwable t) {
                Log.e("BookingDetailsFragment", "Error checking canReview: " + t.getMessage());
            }
        });
    }

    private void populateData() {
        if (booking.getProviderInfo() != null) providerNameText.setText(booking.getProviderInfo().getName());
        bookingIdText.setText("Mã đặt vé: " + booking.getId());
        statusText.setText(getStatusText(booking.getPaymentStatus(), booking.getApprovalStatus()));
        paymentMethodText.setText("Phương thức: " + getPaymentMethodText(booking.getPaymentMethod()));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        totalPriceText.setText("Tổng tiền: " + currencyFormat.format(booking.getTotalPrice()));
        createdAtText.setText("Ngày đặt: " + formatDateTime(booking.getCreatedAt()));

        if (booking.getTripInfo() != null) {
            routeText.setText(booking.getTripInfo().getOrigin() + " → " + booking.getTripInfo().getDestination());
            departureTimeText.setText("Khởi hành: " + formatDateTime(booking.getTripInfo().getDepartureTime()));
        }

        if (booking.getUserInfo() != null) {
            userNameText.setText("Họ tên: " + booking.getUserInfo().getName());
            userEmailText.setText("Email: " + booking.getUserInfo().getEmail());
            userPhoneText.setText("Số điện thoại: " + booking.getUserInfo().getPhoneNumber());
        }

        if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
            TicketDetailsAdapter adapter = new TicketDetailsAdapter(booking.getTickets());
            ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ticketsRecyclerView.setAdapter(adapter);
        }

        if (booking.getReview() != null) {
            BookingHistoryItem.ReviewInfo review = booking.getReview();
            existingReviewId = review.getId();
            ratingBar.setRating(review.getRating());
            commentEditText.setText(review.getComment());
        } else if (getReviewPostedFlag()) {
            float savedRating = getPrefs().getFloat(KEY_SAVED_REVIEW_RATING_PREFIX + booking.getId(), 0);
            String savedComment = getPrefs().getString(KEY_SAVED_REVIEW_COMMENT_PREFIX + booking.getId(), "");
            String savedReviewId = getPrefs().getString(KEY_SAVED_REVIEW_ID_PREFIX + booking.getId(), null);

            ratingBar.setRating(savedRating);
            commentEditText.setText(savedComment);
            if (savedReviewId != null) {
                existingReviewId = savedReviewId;
            }
        } else {
            loadDraftReview();
        }

        submitReviewButton.setText(existingReviewId == null ? "Gửi đánh giá" : "Cập nhật đánh giá");
        submitReviewButton.setEnabled(true);
        ratingBar.setIsIndicator(false);
        commentEditText.setEnabled(true);
    }

    private void handleReviewSubmission() {
        if (existingReviewId == null) {
            createReview();
        } else {
            updateReview();
        }
    }

    private void createReview() {
        if (booking.getTripInfo() == null || booking.getTripInfo().getId() == null) {
            Toast.makeText(getContext(), "Không tìm thấy chuyến đi.", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = ratingBar.getRating();
        if (rating == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn số sao.", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = commentEditText.getText().toString().trim();
        submitReviewButton.setEnabled(false);
        CreateReviewRequest request = new CreateReviewRequest(booking.getTripInfo().getId(), (int) rating, comment);

        apiService.createReview(request).enqueue(new Callback<CreateReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateReviewResponse> call, @NonNull Response<CreateReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();

                    BookingHistoryItem.ReviewInfo review = new BookingHistoryItem.ReviewInfo();
                    if (response.body().getData() != null) {
                        review.setId(response.body().getData().getId());
                    }
                    review.setRating(rating);
                    review.setComment(comment);

                    booking.setReview(review);
                    existingReviewId = review.getId();
                    submitReviewButton.setText("Cập nhật đánh giá");
                    setReviewPostedFlag(true);
                    saveRealReview(rating, comment);
                    clearDraftReview();
                } else {
                    Toast.makeText(getContext(), "Gửi thất bại", Toast.LENGTH_SHORT).show();
                }
                submitReviewButton.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<CreateReviewResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                submitReviewButton.setEnabled(true);
            }
        });
    }

    private void updateReview() {
        if (existingReviewId == null) return;

        float rating = ratingBar.getRating();
        if (rating == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn số sao.", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = commentEditText.getText().toString().trim();
        submitReviewButton.setEnabled(false);
        UpdateReviewRequest request = new UpdateReviewRequest((int) rating, comment);

        apiService.updateReview(existingReviewId, request).enqueue(new Callback<CreateReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateReviewResponse> call, @NonNull Response<CreateReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Cập nhật đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    if (booking.getReview() != null) {
                        booking.getReview().setRating(rating);
                        booking.getReview().setComment(comment);
                    }
                    saveRealReview(rating, comment);
                    clearDraftReview();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
                submitReviewButton.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<CreateReviewResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                submitReviewButton.setEnabled(true);
            }
        });
    }

    private void saveRealReview(float rating, String comment) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putFloat(KEY_SAVED_REVIEW_RATING_PREFIX + booking.getId(), rating);
        editor.putString(KEY_SAVED_REVIEW_COMMENT_PREFIX + booking.getId(), comment);
        if (existingReviewId != null) {
            editor.putString(KEY_SAVED_REVIEW_ID_PREFIX + booking.getId(), existingReviewId);
        }
        editor.apply();
    }

    private String getStatusText(String paymentStatus, String approvalStatus) {
        String payment = "N/A";
        if (paymentStatus != null) {
            switch (paymentStatus) {
                case "completed": payment = "Đã thanh toán"; break;
                case "pending": payment = "Chờ thanh toán"; break;
                case "expired": payment = "Hết hạn"; break;
                default: payment = paymentStatus;
            }
        }
        String approval = "N/A";
        if (approvalStatus != null) {
            switch (approvalStatus) {
                case "confirmed_by_provider": approval = "Đã xác nhận"; break;
                case "pending_approval": approval = "Chờ xác nhận"; break;
                case "cancelled": approval = "Đã hủy"; break;
                default: approval = approvalStatus;
            }
        }
        return payment + " • " + approval;
    }

    private String getPaymentMethodText(String paymentMethod) {
        if (paymentMethod == null) return "N/A";
        switch (paymentMethod) {
            case "cash": return "Tiền mặt";
            case "bank_transfer": return "Chuyển khoản";
            case "vnpay": return "VNPAY";
            default: return paymentMethod;
        }
    }

    private String formatDateTime(String utcDateString) {
        if (utcDateString == null) return "N/A";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDateString);
            SimpleDateFormat localFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return localFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private SharedPreferences getPrefs() {
        return requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void saveDraftReview() {
        if (!getReviewPostedFlag()) {
            SharedPreferences.Editor editor = getPrefs().edit();
            editor.putFloat(KEY_DRAFT_RATING_PREFIX + booking.getId(), ratingBar.getRating());
            editor.putString(KEY_DRAFT_COMMENT_PREFIX + booking.getId(), commentEditText.getText().toString().trim());
            editor.apply();
        }
    }

    private void loadDraftReview() {
        SharedPreferences prefs = getPrefs();
        float savedRating = prefs.getFloat(KEY_DRAFT_RATING_PREFIX + booking.getId(), 0f);
        String savedComment = prefs.getString(KEY_DRAFT_COMMENT_PREFIX + booking.getId(), "");
        if (savedRating > 0 || !TextUtils.isEmpty(savedComment)) {
            ratingBar.setRating(savedRating);
            commentEditText.setText(savedComment);
        }
    }

    private void clearDraftReview() {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.remove(KEY_DRAFT_RATING_PREFIX + booking.getId());
        editor.remove(KEY_DRAFT_COMMENT_PREFIX + booking.getId());
        editor.apply();
    }

    private void setReviewPostedFlag(boolean posted) {
        getPrefs().edit().putBoolean(KEY_REVIEW_POSTED_PREFIX + booking.getId(), posted).apply();
    }

    private boolean getReviewPostedFlag() {
        return getPrefs().getBoolean(KEY_REVIEW_POSTED_PREFIX + booking.getId(), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveDraftReview();
    }


    private void createPaymentUrl(String bookingId) {
        CreatePaymentUrlRequest request = new CreatePaymentUrlRequest(bookingId);
        apiService.createPaymentUrl(request).enqueue(new Callback<CreatePaymentUrlResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreatePaymentUrlResponse> call, @NonNull Response<CreatePaymentUrlResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String paymentUrl = response.body().getPaymentUrl();
                    if (paymentUrl != null && !paymentUrl.isEmpty()) {
                        openVnpaySdk(paymentUrl, bookingId);

                    } else {
                        Toast.makeText(getContext(), "Received an empty payment URL.", Toast.LENGTH_LONG).show();
                        paymentButton.setEnabled(true);
                    }
                } else {
                    String errorMsg = "Failed to create payment URL.";
                    if (response.body() != null) {
                        errorMsg += " " + response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    paymentButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreatePaymentUrlResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error while creating payment URL.", Toast.LENGTH_SHORT).show();
                paymentButton.setEnabled(true);
            }
        });
    }

    private void openVnpaySdk(String paymentUrl, String bookingId) {
        Intent intent = new Intent(requireActivity(), VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl);
        intent.putExtra("tmn_code", "EX6ATLAM");
        intent.putExtra("scheme", "paymentresult");
        intent.putExtra("is_sandbox", true);

        VNP_AuthenticationActivity.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
            @Override
            public void sdkAction(String action) {
                if (action == null) return;
                Log.d("VNPAY_SDK", "SDK Action: " + action);

                switch (action) {
                    case "AppBackAction":
                        Toast.makeText(getActivity(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
                        if (getView() != null) {
                            Navigation.findNavController(getView()).popBackStack();
                        }
                        break;
                    case "CallMobileBankingApp":
                        savePendingTransaction(bookingId);
                        Toast.makeText(getActivity(), "Redirecting to banking app...", Toast.LENGTH_SHORT).show();
                        break;
                    case "WebBackAction":
                    case "FailedBackAction":
                    case "SuccessBackAction":
                        Intent resultIntent = new Intent(getActivity(), PaymentResultActivity.class);
                        Uri resultData = Uri.parse("paymentresult://payment?bookingId=" + bookingId + "&action=" + action);
                        resultIntent.setData(resultData);
                        startActivity(resultIntent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        break;
                    default:
                        Toast.makeText(getActivity(), "Unknown VNPAY action", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        startActivity(intent);
    }

    private void savePendingTransaction(String transactionId) {
        if (getContext() != null && transactionId != null) {
            getContext().getSharedPreferences("PaymentPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("PENDING_TRANSACTION_ID", transactionId)
                    .apply();
        }
    }
}
