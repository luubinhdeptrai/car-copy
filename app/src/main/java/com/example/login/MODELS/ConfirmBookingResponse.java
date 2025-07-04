package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class ConfirmBookingResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    // === THAY ĐỔI: Thêm trường 'bookingId' hoặc 'data' để hứng ID của Booking ===
    // Giả sử backend trả về _id của booking trong trường 'data'
    // Hoặc, nếu backend trả về một Booking object, bạn có thể tạo Booking.java model.
    // Dựa trên cách ConfirmBookingController trả về, nó trả về Booking object.
    // Vì vậy, chúng ta sẽ cần một Booking.java model.

    @SerializedName("data") // Backend trả về đối tượng Booking đầy đủ trong trường 'data'
    private Booking data; // THAY ĐỔI: Giả định backend trả về đối tượng Booking
    // ========================================================================

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Booking getData() { // THAY ĐỔI: Trả về đối tượng Booking
        return data;
    }
}