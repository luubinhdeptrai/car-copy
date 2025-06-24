// File: com/example/login/VIEW/SelectTripFragment.java
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

import com.example.login.ADAPTERS.*;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;

public class SelectTripFragment extends Fragment {

    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> trips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ RecyclerView từ view của Fragment
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Lấy dữ liệu được gửi từ BuyTicketFragment
        if (getArguments() != null) {
            trips = (List<Trip>) getArguments().getSerializable("TRIPS_RESULT");
        }

        // 3. Nếu không có dữ liệu nào được gửi qua hoặc danh sách rỗng, khởi tạo một danh sách rỗng để tránh lỗi
        if (trips == null) {
            trips = new ArrayList<>();
        }

        // Hiển thị thông báo nếu không tìm thấy chuyến đi
        if (trips.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy chuyến đi nào phù hợp.", Toast.LENGTH_LONG).show();
        }

        // 4. Khởi tạo Adapter với danh sách trips đã nhận được
        tripAdapter = new TripAdapter(getContext(), trips);

        // 5. Thiết lập Adapter cho RecyclerView
        tripRecyclerView.setAdapter(tripAdapter);
    }
}