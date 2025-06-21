package com.example.login.VIEW;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.login.R; // Thay bằng package của bạn

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tìm NavHostFragment và lấy NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Tìm BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);

        // Kết nối BottomNavigationView với NavController
        // Việc này sẽ tự động xử lý việc chuyển đổi fragment khi nhấn vào menu
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Xử lý sự kiện click cho nút FAB
//        FloatingActionButton fabPayment = findViewById(R.id.fab_payment);
//        fabPayment.setOnClickListener(v -> {
//            // Điều hướng đến màn hình thanh toán
//            navController.navigate(R.id.paymentFragment);
//        });
    }
}