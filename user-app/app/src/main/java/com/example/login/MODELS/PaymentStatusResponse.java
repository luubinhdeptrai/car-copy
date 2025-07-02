package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class PaymentStatusResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private PaymentStatusData data;

    public String getStatus() {
        return status;
    }

    public PaymentStatusData getData() {
        return data;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public static class PaymentStatusData {
        @SerializedName("_id")
        private String id;

        @SerializedName("paymentStatus")
        private String paymentStatus;

        public String getId() {
            return id;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }
    }
}
