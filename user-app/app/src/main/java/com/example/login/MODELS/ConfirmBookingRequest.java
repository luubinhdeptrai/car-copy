package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConfirmBookingRequest {
    @SerializedName("ticketIds")
    private List<String> ticketIds;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    public ConfirmBookingRequest(List<String> ticketIds, String paymentMethod) {
        this.ticketIds = ticketIds;
        this.paymentMethod = paymentMethod;
    }
}