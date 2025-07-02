package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.ADAPTERS.BookingHistoryAdapter;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.BookingHistoryItem;
import com.example.login.MODELS.BookingHistoryResponse;
import com.example.login.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartingSoonTabFragment extends Fragment implements BookingHistoryAdapter.OnBookingClickListener {

    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private ApiService apiService;
    private List<BookingHistoryItem> allBookings = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        
        apiService = ApiClient.getAuthAPI(getContext());
        fetchBookingHistory();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_history);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyStateText = view.findViewById(R.id.empty_state_text);
    }

    private void setupRecyclerView() {
        adapter = new BookingHistoryAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void fetchBookingHistory() {
        showLoading(true);
        
        apiService.getMyBookingHistory().enqueue(new Callback<BookingHistoryResponse>() {
            @Override
            public void onResponse(Call<BookingHistoryResponse> call, Response<BookingHistoryResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allBookings = response.body().getData();
                    filterAndDisplayBookings();
                } else {
                    showError("Không thể tải lịch sử đặt vé");
                }
            }

            @Override
            public void onFailure(Call<BookingHistoryResponse> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void filterAndDisplayBookings() {
        List<BookingHistoryItem> filteredBookings = new ArrayList<>();
        Date currentTime = new Date();

        for (BookingHistoryItem booking : allBookings) {
            if (booking.getTripInfo() != null && booking.getTripInfo().getDepartureTime() != null) {
                Date departureTime = parseDepartureTime(booking.getTripInfo().getDepartureTime());
                // Filter for future bookings with confirmed_by_provider status
                if (departureTime != null && 
                    departureTime.after(currentTime) && 
                    "confirmed_by_provider".equals(booking.getApprovalStatus())) {
                    filteredBookings.add(booking);
                }
            }
        }

        if (filteredBookings.isEmpty()) {
            showEmptyState("Không có chuyến đi sắp tới đã được xác nhận");
        } else {
            showBookings(filteredBookings);
        }
    }

    private Date parseDepartureTime(String utcDateString) {
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return utcFormat.parse(utcDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void showBookings(List<BookingHistoryItem> bookings) {
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        adapter.updateBookings(bookings);
    }

    private void showEmptyState(String message) {
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateText.setText(message);
    }

    private void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        showEmptyState("Không thể tải dữ liệu");
    }

    @Override
    public void onBookingClick(BookingHistoryItem booking) {
        // Navigate to booking details
        Bundle bundle = new Bundle();
        bundle.putSerializable("BOOKING_DETAILS", booking);
        Navigation.findNavController(requireView()).navigate(R.id.action_history_to_bookingDetails, bundle);
    }
}