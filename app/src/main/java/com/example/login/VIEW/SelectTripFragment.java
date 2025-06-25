package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.ADAPTERS.DateAdapter;
import com.example.login.ADAPTERS.TripAdapter;
import com.example.login.MODELS.DateModel;
import com.example.login.MODELS.Trip;
import com.example.login.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectTripFragment extends Fragment {

    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> trips;
    private RecyclerView dateRecyclerView;
    private DateAdapter dateAdapter;
    private List<DateModel> dates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Nhận dữ liệu trước ---
        long selectedDateMillis = System.currentTimeMillis(); // Mặc định là hôm nay
        if (getArguments() != null) {
            trips = (List<Trip>) getArguments().getSerializable("TRIPS_RESULT");
            selectedDateMillis = getArguments().getLong("SELECTED_DATE_MILLIS", selectedDateMillis);
        }

        // --- Thiết lập thanh chọn ngày ---
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        setupDateRecyclerView(selectedDateMillis);

        // --- Thiết lập danh sách chuyến đi ---
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (trips == null) {
            trips = new ArrayList<>();
        }
        if (trips.isEmpty()) {
            Toast.makeText(getContext(), "No suitable trips found.", Toast.LENGTH_LONG).show();
        }
        tripAdapter = new TripAdapter(getContext(), trips);
        tripRecyclerView.setAdapter(tripAdapter);
    }

    private void setupDateRecyclerView(long selectedDateMillis) {
        dates = new ArrayList<>();
        Calendar iterator = Calendar.getInstance(); // Bắt đầu từ ngày hôm nay
        normalizeCalendar(iterator);

        // Tạo một danh sách ngày dài cho tương lai (ví dụ 365 ngày)
        for (int i = 0; i < 365; i++) {
            dates.add(new DateModel(iterator.getTime()));
            iterator.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Tính toán vị trí của ngày được chọn
        Calendar today = Calendar.getInstance();
        normalizeCalendar(today);
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(selectedDateMillis);
        normalizeCalendar(selectedDate);

        long diffInMillis = selectedDate.getTimeInMillis() - today.getTimeInMillis();
        int initialSelectedPosition = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);

        // Đảm bảo vị trí hợp lệ
        if (initialSelectedPosition < 0) {
            initialSelectedPosition = 0;
        }

        // Khởi tạo Adapter với vị trí đã tính toán
        dateAdapter = new DateAdapter(getContext(), dates, initialSelectedPosition);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);

        // SỬA LỖI: Tạo một biến final để lambda có thể sử dụng
        final int positionToScroll = initialSelectedPosition;

        // Cuộn đến vị trí của ngày được chọn
        dateRecyclerView.post(() -> layoutManager.scrollToPositionWithOffset(positionToScroll, 150));
    }

    private void normalizeCalendar(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
    