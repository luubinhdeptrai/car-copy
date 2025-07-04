package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Seat implements Serializable {
    @SerializedName("_id") // Backend trả về _id cho Ticket
    private String id; // Holds the ticket ID

    @SerializedName("seatNumber") // Backend trả về seatNumber
    private String seatNumber;

    @SerializedName("status") // Backend trả về status của Ticket (available, locked, pending_approval, booked)
    private String backendStatus; // Lưu trạng thái gốc từ backend

    // Trường để lưu trạng thái hiển thị trên UI
    private SeatStatus uiStatus; // Không cần SerializedName cho cái này


    // Constructor (cập nhật để bao gồm backendStatus)
    public Seat(String id, String seatNumber, String backendStatus) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.backendStatus = backendStatus;
        // Logic để chuyển đổi backendStatus sang uiStatus
        this.uiStatus = convertBackendStatusToUiStatus(backendStatus);
    }

    // Constructor mặc định cho Gson nếu cần
    public Seat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getBackendStatus() {
        return backendStatus;
    }

    public void setBackendStatus(String backendStatus) {
        this.backendStatus = backendStatus;
        // Cập nhật uiStatus khi backendStatus thay đổi
        this.uiStatus = convertBackendStatusToUiStatus(backendStatus);
    }

    public SeatStatus getUiStatus() { // Đổi tên từ getStatus() sang getUiStatus() để tránh nhầm lẫn
        return uiStatus;
    }

    public void setUiStatus(SeatStatus uiStatus) { // Đổi tên từ setStatus()
        this.uiStatus = uiStatus;
    }

    private SeatStatus convertBackendStatusToUiStatus(String status) {
        if ("booked".equals(status) || "locked".equals(status) || "pending_approval".equals(status)) {
            return SeatStatus.SOLD_OUT;
        } else {
            return SeatStatus.AVAILABLE;
        }
    }
}