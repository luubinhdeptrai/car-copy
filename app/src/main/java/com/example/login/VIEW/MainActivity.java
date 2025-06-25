package com.example.login.VIEW;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.login.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tìm NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Tìm BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);

        // Kết nối BottomNavigationView với NavController
        // Việc này sẽ tự động xử lý việc chuyển đổi fragment khi nhấn vào menu
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Không còn FloatingActionButton nên không cần listener nữa
    }
}