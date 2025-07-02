package com.example.login.VIEW;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.login.R; // Thay bằng package của bạn

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tìm NavHostFragment và lấy NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Tìm BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);

        // --- SỬA LỖI: Thay thế setupWithNavController bằng listener tùy chỉnh ---
        bottomNav.setOnItemSelectedListener(item -> {
            // Tạo các tùy chọn điều hướng
            NavOptions.Builder navOptionsBuilder = new NavOptions.Builder();

            // Tránh tạo lại màn hình nếu đã ở trên cùng
            navOptionsBuilder.setLaunchSingleTop(true);

            // Xóa tất cả các màn hình con trên stack khi chuyển tab
            // để luôn quay về màn hình gốc của tab đó.
            navOptionsBuilder.setPopUpTo(item.getItemId(), true);

            NavOptions navOptions = navOptionsBuilder.build();

            // Thực hiện điều hướng với các tùy chọn đã thiết lập
            navController.navigate(item.getItemId(), null, navOptions);

            return true;
        });
    }
}
    