package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MODELS.BookingHistoryItem;
import com.example.login.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BookingDetailsFragment extends Fragment {

    private BookingHistoryItem booking;
    private Toolbar toolbar;
    private TextView providerNameText, bookingIdText, statusText, paymentMethodText, 
                     totalPriceText, createdAtText, routeText, departureTimeText,
                     userNameText, userEmailText, userPhoneText;
    private RecyclerView ticketsRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Navigation.findNavController(view).popBackStack();
            return;
        }

        initViews(view);
        setupToolbar();
        populateData();
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
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        toolbar.setTitle("Chi tiết đặt vé");
    }

    private void populateData() {
        // Provider name
        if (booking.getProviderInfo() != null) {
            providerNameText.setText(booking.getProviderInfo().getName());
        }

        // Booking ID
        bookingIdText.setText("Mã đặt vé: " + booking.getId());

        // Status
        String statusText = getStatusText(booking.getPaymentStatus(), booking.getApprovalStatus());
        this.statusText.setText(statusText);

        // Payment method
        String paymentMethod = getPaymentMethodText(booking.getPaymentMethod());
        paymentMethodText.setText("Phương thức: " + paymentMethod);

        // Total price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        String formattedPrice = currencyFormat.format(booking.getTotalPrice());
        totalPriceText.setText("Tổng tiền: " + formattedPrice);

        // Created at
        String createdAt = formatDateTime(booking.getCreatedAt());
        createdAtText.setText("Ngày đặt: " + createdAt);

        // Trip info
        if (booking.getTripInfo() != null) {
            String route = booking.getTripInfo().getOrigin() + " → " + booking.getTripInfo().getDestination();
            routeText.setText(route);

            String departureTime = formatDateTime(booking.getTripInfo().getDepartureTime());
            departureTimeText.setText("Khởi hành: " + departureTime);
        }

        // User info
        if (booking.getUserInfo() != null) {
            userNameText.setText("Họ tên: " + booking.getUserInfo().getName());
            userEmailText.setText("Email: " + booking.getUserInfo().getEmail());
            userPhoneText.setText("Số điện thoại: " + booking.getUserInfo().getPhoneNumber());
        }

        // Setup tickets RecyclerView
        if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
            TicketDetailsAdapter adapter = new TicketDetailsAdapter(booking.getTickets());
            ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ticketsRecyclerView.setAdapter(adapter);
        }
    }

    private String getStatusText(String paymentStatus, String approvalStatus) {
        String payment = "";
        String approval = "";
        
        switch (paymentStatus) {
            case "completed":
                payment = "Đã thanh toán";
                break;
            case "pending":
                payment = "Chờ thanh toán";
                break;
            case "expired":
                payment = "Hết hạn chuyển khoản";
                break;
            default:
                payment = paymentStatus;
        }

        switch (approvalStatus) {
            case "confirmed_by_provider":
                approval = "Đã xác nhận";
                break;
            case "pending_approval":
                approval = "Chờ xác nhận";
                break;
            case "cancelled":
                approval = "Từ chối";
                break;
            default:
                approval = approvalStatus;
        }

        return payment + " • " + approval;
    }

    private String getPaymentMethodText(String paymentMethod) {
        switch (paymentMethod) {
            case "cash":
                return "Tiền mặt";
            case "bank_transfer":
                return "Thanh toán VNPAY";
            case "vnpay":
                return "VNPay";
            default:
                return paymentMethod;
        }
    }

    private String formatDateTime(String utcDateString) {
        if (utcDateString == null) return "N/A";
        
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = utcFormat.parse(utcDateString);

            SimpleDateFormat localFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.US);
            localFormat.setTimeZone(TimeZone.getDefault());
            return localFormat.format(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}