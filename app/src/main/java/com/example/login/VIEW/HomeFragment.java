package com.example.login.VIEW;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView; // << THÊM IMPORT
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.API.ApiClient; // << THÊM IMPORT
import com.example.login.API.ApiService; // << THÊM IMPORT
import com.example.login.MODELS.ProfileResponse; // << THÊM IMPORT
import com.example.login.MODELS.User; // << THÊM IMPORT
import com.example.login.R;

import retrofit2.Call; // << THÊM IMPORT
import retrofit2.Callback; // << THÊM IMPORT
import retrofit2.Response; // << THÊM IMPORT

public class HomeFragment extends Fragment {

    // --- BIẾN MỚI ĐƯỢC THÊM ---
    private ApiService apiService;
    private TextView welcomeTextView;
    // -------------------------

    // --- PHƯƠNG THỨC MỚI ĐƯỢC THÊM ĐỂ KHỞI TẠO API SERVICE ---
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo ApiService sử dụng context của Fragment
        // Dùng getAuthAPI vì cần token để xác thực người dùng
        apiService = ApiClient.getAuthAPI(requireContext());
    }
    // ----------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout XML lên thành một đối tượng View
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- CẬP NHẬT: Ánh xạ TextView và gọi hàm lấy thông tin user ---
        welcomeTextView = view.findViewById(R.id.welcome_text);
        fetchAndDisplayUserInfo();
        // --------------------------------------------------------

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

    // --- PHƯƠNG THỨC MỚI ĐƯỢC THÊM, TƯƠNG TỰ CONFIRMATIONFRAGMENT ---
    private void fetchAndDisplayUserInfo() {
        // Gọi API endpoint /api/users/me
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                // Kiểm tra cuộc gọi có thành công và có dữ liệu trả về không
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    // Lấy tên đầy đủ của user và cập nhật TextView
                    if (user != null && user.getFullname() != null) {
                        welcomeTextView.setText("Welcome,\n" + user.getFullname());
                    } else {
                        welcomeTextView.setText("Welcome,\nUser");
                    }
                } else {
                    // Xử lý lỗi khi không lấy được thông tin
                    welcomeTextView.setText("Welcome,\nUser");
                    Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                // Xử lý lỗi mạng
                welcomeTextView.setText("Welcome,\nUser");
                Toast.makeText(getContext(), "Lỗi mạng, không thể tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // -------------------------------------------------------------
}