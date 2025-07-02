package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Seat implements Serializable {
    @SerializedName("_id")
    private String id; // Holds the ticket ID

    private String seatNumber;
    private SeatStatus status;
    private String backendStatus;

    // You might need to adjust your constructors
    public Seat(String id, String seatNumber, String backendStatus) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.backendStatus = backendStatus;
        if ("booked".equals(backendStatus) || "locked".equals(backendStatus) || "pending_approval".equals(backendStatus)) {
            this.status = SeatStatus.SOLD_OUT;
        } else {
            this.status = SeatStatus.AVAILABLE;
        }
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