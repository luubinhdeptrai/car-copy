package com.example.login.VIEW;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;

public class BookingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chỉ cần set layout, NavHostFragment sẽ tự động quản lý các fragment
        setContentView(R.layout.activity_booking);
    }
}
