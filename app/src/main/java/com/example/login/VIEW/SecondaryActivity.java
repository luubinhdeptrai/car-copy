package com.example.login.VIEW;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Import Fragment của bạn
import com.example.login.R;
import com.example.login.VIEW.SelectTripFragment;

public class SecondaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondary_layout);

        // Chỉ tải Fragment khi Activity được tạo lần đầu.
        // Điều này ngăn việc tạo lại Fragment khi có thay đổi cấu hình (như xoay màn hình).
        if (savedInstanceState == null) {
            loadFragment(new SelectTripFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        // Lấy trình quản lý Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Bắt đầu một giao dịch
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Thay thế nội dung của container bằng Fragment mới
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // Xác nhận (commit) giao dịch
        fragmentTransaction.commit();
    }
}
