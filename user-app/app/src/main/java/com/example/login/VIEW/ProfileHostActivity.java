package com.example.login.VIEW;// package com.example.login.VIEW; // Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import com.example.login.R; // Thay bằng package của bạn

public class ProfileHostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_host);

        // Tìm NavHostFragment và lấy NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profile_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Lấy graph từ NavController
        // Chúng ta sẽ tự cấu hình graph này thay vì để NavHostFragment tự làm
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.profile_nav_graph);

        // Lấy thông tin đích đến từ Intent mà ProfileFragment gửi qua
        Intent intent = getIntent();

        // Kiểm tra xem có phải người dùng muốn vào màn hình Settings không
        if (intent != null && "settings".equals(intent.getStringExtra("START_DESTINATION"))) {
            // Nếu đúng, thì thiết lập màn hình bắt đầu của graph là SettingsFragment
            navGraph.setStartDestination(R.id.settingsFragment);
        } else {
            // Nếu không, hoặc khi nhấn vào thông tin cá nhân,
            // màn hình bắt đầu mặc định sẽ là UserDetailsFragment
            navGraph.setStartDestination(R.id.userDetailsFragment);
        }

        // Gán graph đã được cấu hình lại cho NavController
        // NavController bây giờ sẽ tự động hiển thị đúng Fragment bắt đầu
        navController.setGraph(navGraph);
    }
}
    