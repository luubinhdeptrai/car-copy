package com.example.login.VIEW;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.R; // Thay bằng package của bạn

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout XML lên thành một đối tượng View
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các View từ layout
        LinearLayout profileInfoLayout = view.findViewById(R.id.profile_info_layout);
        TextView settingMenuItem = view.findViewById(R.id.setting_menu_item);
        TextView supportMenuItem = view.findViewById(R.id.support_menu_item);
        TextView deleteAccountMenuItem = view.findViewById(R.id.delete_account_menu_item);
        TextView logoutMenuItem = view.findViewById(R.id.logout_menu_item);

        // Bắt sự kiện click vào khu vực thông tin cá nhân
        profileInfoLayout.setOnClickListener(v -> {
            // Khởi chạy ProfileHostActivity và yêu cầu nó hiển thị UserDetailsFragment
            Intent intent = new Intent(getActivity(), ProfileHostActivity.class);
            // "START_DESTINATION" là một key để ProfileHostActivity biết cần mở màn hình nào
            intent.putExtra("START_DESTINATION", "user_details");
            startActivity(intent);
        });

        // Bắt sự kiện click vào mục "Setting"
        settingMenuItem.setOnClickListener(v -> {
            // Khởi chạy ProfileHostActivity và yêu cầu nó hiển thị SettingsFragment
            Intent intent = new Intent(getActivity(), ProfileHostActivity.class);
            intent.putExtra("START_DESTINATION", "settings");
            startActivity(intent);
        });

        // Bắt sự kiện cho các mục còn lại
        supportMenuItem.setOnClickListener(v -> {
            // TODO: Mở Zalo hoặc màn hình hỗ trợ
            Toast.makeText(getContext(), "Quick Support Clicked", Toast.LENGTH_SHORT).show();
        });

        deleteAccountMenuItem.setOnClickListener(v -> {
            // TODO: Hiển thị hộp thoại xác nhận xóa tài khoản
            Toast.makeText(getContext(), "Delete Account Clicked", Toast.LENGTH_SHORT).show();
        });

        logoutMenuItem.setOnClickListener(v -> {
            // TODO: Xử lý logic đăng xuất và quay về màn hình đăng nhập
            Toast.makeText(getContext(), "Logout Clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
    