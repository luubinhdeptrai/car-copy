package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.example.login.R; // Thay bằng package của bạn
import java.util.ArrayList;
import java.util.List;

public class BuyTicketFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout XML lên thành một đối tượng View
        return inflater.inflate(R.layout.fragment_buy_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Thiết lập cho Spinner ---
        Spinner ticketNumberSpinner = view.findViewById(R.id.ticket_number_spinner);

        // 1. Tạo dữ liệu cho Spinner (ví dụ: từ 1 đến 10 vé)
        List<String> ticketNumbers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ticketNumbers.add(String.valueOf(i));
        }

        // 2. Tạo một ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item, // Layout cho item khi được chọn
                ticketNumbers
        );

        // 3. Set layout cho danh sách dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 4. Kết nối adapter với Spinner
        ticketNumberSpinner.setAdapter(adapter);


        // --- Logic cho nút "Round Trip" ---
        SwitchMaterial roundTripSwitch = view.findViewById(R.id.round_trip_switch);
        LinearLayout returnDateLayout = view.findViewById(R.id.return_date_layout);

        roundTripSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Nếu bật, hiện ô chọn ngày về
                returnDateLayout.setVisibility(View.VISIBLE);
            } else {
                // Nếu tắt, ẩn ô chọn ngày về
                returnDateLayout.setVisibility(View.GONE);
            }
        });


        // --- Logic cho nút Search và nút Back ---
        Button searchButton = view.findViewById(R.id.search_route_button);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        searchButton.setOnClickListener(v -> {
            // Điều hướng đến màn hình chọn chuyến xe
            Navigation.findNavController(view).navigate(R.id.action_buyTicket_to_selectTrip);
        });

        toolbar.setNavigationOnClickListener(v -> {
            // Quay lại màn hình trước đó
            Navigation.findNavController(view).popBackStack();
        });
    }
}