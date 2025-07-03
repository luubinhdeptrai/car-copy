package com.example.login.VIEW;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.ADAPTERS.AdBannerAdapter;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.User;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ApiService apiService;
    private TextView welcomeTextView;
    private RecyclerView adBannerRecyclerView;
    private AdBannerAdapter adBannerAdapter;
    private List<Integer> adImages;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        welcomeTextView = view.findViewById(R.id.welcome_text);
        fetchAndDisplayUserInfo();

        LinearLayout futaBusTicketLayout = view.findViewById(R.id.futa_bus_ticket_layout);
        ImageView customerSupportLayout = view.findViewById(R.id.help_icon);
        LinearLayout expressLayout = view.findViewById(R.id.express_layout);

        futaBusTicketLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BookingActivity.class);
            startActivity(intent);
        });

        // CẬP NHẬT: Bắt sự kiện click cho "Hỗ trợ khách hàng" để mở CustomerSupportActivity
        customerSupportLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CustomerSupportActivity.class);
            startActivity(intent);
        });

        expressLayout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
        adBannerRecyclerView = view.findViewById(R.id.ad_banner_recycler_view);
        setupAdBanner();
    }

    private void fetchAndDisplayUserInfo() {
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null && user.getFullname() != null) {
                        welcomeTextView.setText("Chào mừng,\n" + user.getFullname());
                    } else {
                        welcomeTextView.setText("Chào mừng,\nUser");
                    }
                } else {
                    welcomeTextView.setText("Chào mừng,\nUser");
                    Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                welcomeTextView.setText("Chào mừng,\nUser");
                Toast.makeText(getContext(), "Lỗi mạng, không thể tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupAdBanner() {
        // Tạo dữ liệu mẫu
        adImages = new ArrayList<>();
        // Bạn hãy chắc chắn đã có các file ảnh này trong thư mục res/drawable
        adImages.add(R.drawable.ads); // Giả sử tên file là ad_banner.png
        adImages.add(R.drawable.ads); // Thêm ảnh thứ 2 (có thể là ảnh khác)
        adImages.add(R.drawable.ads); // Thêm ảnh thứ 3

        // Thiết lập Adapter
        adBannerAdapter = new AdBannerAdapter(getContext(), adImages);

        // Thiết lập LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adBannerRecyclerView.setLayoutManager(layoutManager);

        // Gán Adapter cho RecyclerView
        adBannerRecyclerView.setAdapter(adBannerAdapter);
    }
}