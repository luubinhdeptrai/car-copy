package com.example.login.VIEW;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.login.R;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout XML lên thành một đối tượng View
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm các layout có thể click bằng ID
        LinearLayout futaBusTicketLayout = view.findViewById(R.id.futa_bus_ticket_layout);
        LinearLayout cityBusLayout = view.findViewById(R.id.city_bus_layout);
        LinearLayout expressLayout = view.findViewById(R.id.express_layout);

        // Bắt sự kiện click cho "FUTA Bus Ticket"
        futaBusTicketLayout.setOnClickListener(v -> {
            // Khởi chạy BookingActivity bằng Intent
            Intent intent = new Intent(getActivity(), BookingActivity.class);
            startActivity(intent);
        });

        // Bắt sự kiện click cho "City Bus"
        cityBusLayout.setOnClickListener(v -> {
            // TODO: Điều hướng đến màn hình đặt vé xe bus thành phố
            // Hiện tại, chỉ hiển thị thông báo
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // Bắt sự kiện click cho "Express"
        expressLayout.setOnClickListener(v -> {
            // TODO: Điều hướng đến màn hình đặt xe giao hàng
            // Hiện tại, chỉ hiển thị thông báo
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }
}