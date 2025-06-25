package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// import android.widget.Toast; // <-- Không cần Toast ở đây nữa

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

import java.io.Serializable; // <-- Thêm import này
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

    // View cho layout không có dữ liệu
    private View layoutNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    @SuppressWarnings("unchecked") // <-- Bỏ qua cảnh báo ép kiểu không kiểm tra
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        layoutNoData = view.findViewById(R.id.layout_no_data);

        // --- Nhận dữ liệu ---
        long selectedDateMillis = System.currentTimeMillis(); // Mặc định là hôm nay
        if (getArguments() != null) {
            // Xử lý an toàn cho ép kiểu Serializable
            Serializable serializableTrips = getArguments().getSerializable("TRIPS_RESULT");
            if (serializableTrips instanceof List<?>) { // Kiểm tra kiểu dữ liệu trước khi ép
                trips = (List<Trip>) serializableTrips;
            }
            selectedDateMillis = getArguments().getLong("SELECTED_DATE_MILLIS", selectedDateMillis);
        }

        // Đảm bảo trips không bao giờ null
        if (trips == null) {
            trips = new ArrayList<>();
        }

        // --- Thiết lập thanh chọn ngày ---
        setupDateRecyclerView(selectedDateMillis);

        // --- Logic hiển thị danh sách hoặc thông báo "Không tìm thấy" ---
        updateTripList();
    }

    // Tách logic hiển thị ra một hàm riêng
    private void updateTripList() {
        if (trips.isEmpty()) {
            // Không có chuyến đi -> ẩn RecyclerView, hiện layout "Không có dữ liệu"
            tripRecyclerView.setVisibility(View.GONE);
            layoutNoData.setVisibility(View.VISIBLE);
            // Toast.makeText(getContext(), "Không tìm thấy chuyến đi nào phù hợp.", Toast.LENGTH_LONG).show(); // <-- Bỏ Toast này
        } else {
            // Có chuyến đi -> hiện RecyclerView, ẩn layout "Không có dữ liệu"
            tripRecyclerView.setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.GONE);

            // Thiết lập danh sách chuyến đi
            tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            tripAdapter = new TripAdapter(getContext(), trips);
            tripRecyclerView.setAdapter(tripAdapter);
        }
    }

    private void setupDateRecyclerView(long selectedDateMillis) {
        dates = new ArrayList<>();
        Calendar iterator = Calendar.getInstance(); // Bắt đầu từ ngày hôm nay
        normalizeCalendar(iterator);

        // Tạo một danh sách ngày dài cho tương lai (ví dụ 30 ngày)
        for (int i = 0; i < 30; i++) {
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

        if (initialSelectedPosition < 0) {
            initialSelectedPosition = 0;
        }

        // Khởi tạo Adapter với vị trí đã tính toán
        dateAdapter = new DateAdapter(getContext(), dates, initialSelectedPosition);

        // Biến layoutManager đã được khai báo là "final", nên lỗi lambda ở dòng 123
        // (dateRecyclerView.post(() -> layoutManager.scrollToPositionWithOffset(initialSelectedPosition, 150));)
        // đáng lẽ đã được khắc phục.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);

        // Cuộn đến vị trí của ngày được chọn sau khi layout đã được vẽ
        // Offset 150 để căn lề cho đẹp hơn
        // Dòng này đáng lẽ không còn lỗi nếu layoutManager là final
        int finalInitialSelectedPosition = initialSelectedPosition;
        dateRecyclerView.post(() -> layoutManager.scrollToPositionWithOffset(finalInitialSelectedPosition, 150));
    }

    private void normalizeCalendar(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}