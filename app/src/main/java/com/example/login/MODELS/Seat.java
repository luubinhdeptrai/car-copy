package com.example.login.MODELS;

import java.io.Serializable; // Quan trọng: Đảm bảo Seat implement Serializable

public class Seat implements Serializable {
    private String seatNumber;
    private SeatStatus status; // Trạng thái ở frontend (AVAILABLE, SOLD_OUT, SELECTED)
    private String backendStatus; // Thêm trường này để lưu trạng thái từ backend (available, locked, booked, etc.)

    public Seat(String seatNumber, SeatStatus status, String backendStatus) {
        this.seatNumber = seatNumber;
        this.status = status;
        this.backendStatus = backendStatus;
    }

    // Constructor mới chỉ với seatNumber và backendStatus (khi parse từ API)
    public Seat(String seatNumber, String backendStatus) {
        this.seatNumber = seatNumber;
        this.backendStatus = backendStatus;
        // Khởi tạo status mặc định dựa trên backendStatus
        if ("booked".equals(backendStatus) || "locked".equals(backendStatus) || "pending_approval".equals(backendStatus)) {
            this.status = SeatStatus.SOLD_OUT;
        } else {
            this.status = SeatStatus.AVAILABLE;
        }
    }
    private void setStatusFromString(String statusString) {
        if (statusString == null) {
            this.status = SeatStatus.AVAILABLE;
            return;
        }
        switch (statusString.toLowerCase()) {
            case "booked": // Giả sử API trả về "booked" cho ghế đã bán
            case "sold_out":
                this.status = SeatStatus.SOLD_OUT;
                break;
            case "selected": // Trạng thái này chỉ dùng ở phía client
                this.status = SeatStatus.SELECTED;
                break;
            default: // Mặc định là "available"
                this.status = SeatStatus.AVAILABLE;
                break;
        }
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getBackendStatus() {
        return backendStatus;
    }

    public void setBackendStatus(String backendStatus) {
        this.backendStatus = backendStatus;
    }
}