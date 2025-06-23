package com.example.login.VIEW;

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
import androidx.navigation.Navigation;
import com.example.login.R; // Thay bằng package của bạn

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm kiếm các view bằng ID
        LinearLayout profileInfoLayout = view.findViewById(R.id.profile_info_layout);
        TextView settingMenuItem = view.findViewById(R.id.setting_menu_item);

        // Bắt sự kiện click để chuyển sang màn hình UserDetails
        profileInfoLayout.setOnClickListener(v -> {
            // Sử dụng NavController để điều hướng
            Navigation.findNavController(view).navigate(R.id.action_profile_to_userDetails);
        });

        // Bắt sự kiện click để chuyển sang màn hình Settings
        settingMenuItem.setOnClickListener(v -> {
            // Sử dụng NavController để điều hướng
            Navigation.findNavController(view).navigate(R.id.action_profile_to_settings);
        });

        // TODO: Thêm sự kiện click cho các mục còn lại (Support, Delete, Logout)
    }
}
    