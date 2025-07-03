package com.example.login.INTERFACES; // Bạn có thể đặt trong một package riêng

import com.example.login.MODELS.Trip;

/**
 * Interface để lắng nghe sự kiện khi một chuyến đi được chọn trong RecyclerView.
 */
public interface OnTripSelectedListener {
    void onTripSelected(Trip trip);
}
    