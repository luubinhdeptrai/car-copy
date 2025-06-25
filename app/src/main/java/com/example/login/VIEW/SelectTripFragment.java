package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import java.io.Serializable;
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
    private View layoutNoData;
    // <<< THÊM: Khai báo TextView cho tiêu đề lộ trình >>>
    private TextView tvRouteTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        layoutNoData = view.findViewById(R.id.layout_no_data);
        // <<< THÊM: Ánh xạ TextView tiêu đề >>>
        tvRouteTitle = view.findViewById(R.id.tv_route_title);

        // --- Nhận dữ liệu ---
        long selectedDateMillis = System.currentTimeMillis();
        String departure = "";
        String destination = "";

        if (getArguments() != null) {
            Serializable serializableTrips = getArguments().getSerializable("TRIPS_RESULT");
            if (serializableTrips instanceof List) {
                trips = (List<Trip>) serializableTrips;
            }
            selectedDateMillis = getArguments().getLong("SELECTED_DATE_MILLIS", selectedDateMillis);
            // <<< THÊM: Nhận thông tin điểm đi và điểm đến >>>
            departure = getArguments().getString("DEPARTURE_LOCATION", "");
            destination = getArguments().getString("DESTINATION_LOCATION", "");
        }

        // <<< THÊM: Cập nhật tiêu đề lộ trình >>>
        String routeTitle = departure + " → " + destination;
        tvRouteTitle.setText(routeTitle);

        // Đảm bảo trips không bao giờ null
        if (trips == null) {
            trips = new ArrayList<>();
        }

        // --- Thiết lập thanh chọn ngày ---
        setupDateRecyclerView(selectedDateMillis);

        // --- Logic hiển thị danh sách hoặc thông báo "Không tìm thấy" ---
        updateTripList();
    }

    private void updateTripList() {
        if (trips.isEmpty()) {
            tripRecyclerView.setVisibility(View.GONE);
            layoutNoData.setVisibility(View.VISIBLE);
        } else {
            tripRecyclerView.setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.GONE);
            tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            tripAdapter = new TripAdapter(getContext(), trips);
            tripRecyclerView.setAdapter(tripAdapter);
        }
    }

    private void setupDateRecyclerView(long selectedDateMillis) {
        dates = new ArrayList<>();
        Calendar iterator = Calendar.getInstance();
        normalizeCalendar(iterator);
        for (int i = 0; i < 365; i++) {
            dates.add(new DateModel(iterator.getTime()));
            iterator.add(Calendar.DAY_OF_YEAR, 1);
        }
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
        dateAdapter = new DateAdapter(getContext(), dates, initialSelectedPosition);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);
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
    