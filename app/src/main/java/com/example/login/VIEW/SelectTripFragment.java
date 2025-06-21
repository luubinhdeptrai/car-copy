package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.ADAPTERS.TripAdapter;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;

public class SelectTripFragment extends Fragment {

    // Khai báo RecyclerView và Adapter ở đây để có thể truy cập trong các phương thức khác
    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> trips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chỉ inflate view và return nó ở đây
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ RecyclerView từ view của Fragment
        // Phải dùng 'view.findViewById' vì ta đang ở trong Fragment
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view); // <-- Sửa lỗi findViewById

        // 2. Tạo dữ liệu mẫu
        trips = new ArrayList<>();
        // Lưu ý: Constructor của Trip cần các tham số tương ứng
        // Giả sử constructor là: (departure, destination, departureTime, arrivalTime, price, typeSeat, emptySeat, distance, duration)
        trips.add(new Trip("VP Bến Xe Vũng Tàu", "Bến Xe Miền Tây", "17:30", "20:30", 140000, "Chair", 28, 120, 3));
        trips.add(new Trip("VP Bến Xe Vũng Tàu", "Bến Xe Miền Tây", "18:00", "21:00", 140000, "Chair", 17, 120, 3));
        trips.add(new Trip("VP Bến Xe Vũng Tàu", "Bến Xe Miền Đông", "19:00", "22:30", 160000, "Limousine", 10, 130, 3.5));

        // 3. Khởi tạo Adapter
        // Sử dụng getContext() để truyền Context cho Adapter
        tripAdapter = new TripAdapter(getContext(), trips); // <-- Sửa lỗi Context

        // 4. Thiết lập LayoutManager và Adapter cho RecyclerView
        // Sử dụng getContext() để truyền Context cho LinearLayoutManager
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // <-- Sửa lỗi Context
        tripRecyclerView.setAdapter(tripAdapter);
    }
}