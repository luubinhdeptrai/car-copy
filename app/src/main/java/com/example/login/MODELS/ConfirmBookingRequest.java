package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConfirmBookingRequest {
    @SerializedName("ticketIds")
    private List<String> ticketIds;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    // THÊM MỚI: Cần gửi originStopId và destinationStopId cho ConfirmBooking API
    @SerializedName("originStopId")
    private String originStopId;
    @SerializedName("destinationStopId")
    private String destinationStopId;

    public ConfirmBookingRequest(List<String> ticketIds, String paymentMethod, String originStopId, String destinationStopId) {
        this.ticketIds = ticketIds;
        this.paymentMethod = paymentMethod;
        this.originStopId = originStopId;
        this.destinationStopId = destinationStopId;
    }
}