package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.R; // Thay bằng package của bạn

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

        // Tìm layout FUTA Bus Ticket bằng ID
        LinearLayout futaBusTicketLayout = view.findViewById(R.id.futa_bus_ticket_layout);

        // Bắt sự kiện click
        futaBusTicketLayout.setOnClickListener(v -> {
            // Sử dụng NavController để điều hướng theo action đã định nghĩa trong nav_graph.xml
            Navigation.findNavController(view).navigate(R.id.action_home_to_buyTicket);
        });

        // Bạn có thể bắt sự kiện cho các layout khác tương tự
        LinearLayout cityBusLayout = view.findViewById(R.id.city_bus_layout);
        cityBusLayout.setOnClickListener(v -> {
            // TODO: Điều hướng đến màn hình đặt vé xe bus thành phố
            Toast.makeText(getContext(), "City Bus Clicked", Toast.LENGTH_SHORT).show();
        });

        LinearLayout expressLayout = view.findViewById(R.id.express_layout);
        expressLayout.setOnClickListener(v -> {
            // TODO: Điều hướng đến màn hình đặt xe giao hàng
            Toast.makeText(getContext(), "Express Clicked", Toast.LENGTH_SHORT).show();
        });
    }
}