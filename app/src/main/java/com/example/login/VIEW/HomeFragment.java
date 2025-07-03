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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.ADAPTERS.AdBannerAdapter;
import com.example.login.R;
import com.example.login.VIEW.BookingActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView adBannerRecyclerView;
    private AdBannerAdapter adBannerAdapter;
    private List<Integer> adImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bắt sự kiện click cho các loại xe
        LinearLayout futaBusTicketLayout = view.findViewById(R.id.futa_bus_ticket_layout);
        LinearLayout cityBusLayout = view.findViewById(R.id.city_bus_layout);
        LinearLayout expressLayout = view.findViewById(R.id.express_layout);

        futaBusTicketLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BookingActivity.class);
            startActivity(intent);
        });

        cityBusLayout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        expressLayout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // Thiết lập cho RecyclerView quảng cáo
        adBannerRecyclerView = view.findViewById(R.id.ad_banner_recycler_view);
        setupAdBanner();
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