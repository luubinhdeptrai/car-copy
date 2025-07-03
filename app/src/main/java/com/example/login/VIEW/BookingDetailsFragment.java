package com.example.login.VIEW;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.login.VIEW.TicketDetailsAdapter;
import com.example.login.MODELS.BookingHistoryItem;
import com.example.login.MODELS.CanReviewResponse;
import com.example.login.MODELS.CreateReviewRequest;
import com.example.login.MODELS.CreateReviewResponse;
import com.example.login.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsFragment extends Fragment {

    private BookingHistoryItem booking;
    private ApiService apiService;

    // Khai báo các View
    private Toolbar toolbar;
    private TextView providerNameText, bookingIdText, statusText, paymentMethodText,
            totalPriceText, createdAtText, routeText, departureTimeText,
            userNameText, userEmailText, userPhoneText;
    private RecyclerView ticketsRecyclerView;

    // Khai báo các View cho phần đánh giá
    private CardView reviewSection;
    private RatingBar ratingBar;
    private TextInputEditText commentEditText;
    private Button submitReviewButton;

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

        submitReviewButton.setOnClickListener(v -> submitReview());
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
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        toolbar.setTitle("Chi tiết đặt vé");
    }

    private void checkCanReview() {
        reviewSection.setVisibility(View.GONE); // Mặc định ẩn

        if (booking.getTripInfo() == null || booking.getTripInfo().getId() == null) {
            return;
        }

        String tripId = booking.getTripInfo().getId();

        apiService.canReview(tripId).enqueue(new Callback<CanReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<CanReviewResponse> call, @NonNull Response<CanReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isCanReview()) {
                    reviewSection.setVisibility(View.VISIBLE);
                } else {
                    reviewSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CanReviewResponse> call, @NonNull Throwable t) {
                reviewSection.setVisibility(View.GONE);
                Log.e("BookingDetails", "CanReview API call failed: " + t.getMessage());
            }
        });
    }

    private void submitReview() {
        if (booking.getTripInfo() == null || booking.getTripInfo().getId() == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin chuyến đi.", Toast.LENGTH_SHORT).show();
            return;
        }

        String tripId = booking.getTripInfo().getId();
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn số sao đánh giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        submitReviewButton.setEnabled(false);
        Toast.makeText(getContext(), "Đang gửi đánh giá...", Toast.LENGTH_SHORT).show();

        CreateReviewRequest request = new CreateReviewRequest(tripId, (int) rating, comment);

        apiService.createReview(request).enqueue(new Callback<CreateReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateReviewResponse> call, @NonNull Response<CreateReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Gửi đánh giá thành công!", Toast.LENGTH_LONG).show();
                    ratingBar.setIsIndicator(true);
                    commentEditText.setEnabled(false);
                    submitReviewButton.setText("Đã gửi đánh giá");
                } else {
                    Toast.makeText(getContext(), "Gửi đánh giá thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    submitReviewButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateReviewResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                submitReviewButton.setEnabled(true);
            }
        });
    }

    private void populateData() {
        if (booking.getProviderInfo() != null) {
            providerNameText.setText(booking.getProviderInfo().getName());
        }

        bookingIdText.setText("Mã đặt vé: " + booking.getId());

        String statusString = getStatusText(booking.getPaymentStatus(), booking.getApprovalStatus());
        statusText.setText(statusString);

        String paymentMethodString = getPaymentMethodText(booking.getPaymentMethod());
        paymentMethodText.setText("Phương thức: " + paymentMethodString);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        totalPriceText.setText("Tổng tiền: " + currencyFormat.format(booking.getTotalPrice()));

        createdAtText.setText("Ngày đặt: " + formatDateTime(booking.getCreatedAt()));

        if (booking.getTripInfo() != null) {
            String route = booking.getTripInfo().getOrigin() + " → " + booking.getTripInfo().getDestination();
            routeText.setText(route);
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
}