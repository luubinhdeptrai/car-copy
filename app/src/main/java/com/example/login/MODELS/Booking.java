package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {
    @SerializedName("_id")
    private String id; // ID của Booking

    @SerializedName("user")
    private String userId; // ID của User (hoặc User object nếu populate)

    @SerializedName("tickets")
    private List<String> ticketIds; // Danh sách ID các Ticket

    @SerializedName("provider")
    private String providerId; // ID của Provider (hoặc Provider object nếu populate)

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("approvalStatus")
    private String approvalStatus; // pending_approval, confirmed_by_provider, cancelled

    @SerializedName("paymentStatus")
    private String paymentStatus; // pending, completed, failed, expired

    @SerializedName("bookingExpiresAt")
    private String bookingExpiresAtString; // Ngày hết hạn (String từ API)

    @SerializedName("createdAt")
    private String createdAtString; // Ngày tạo (String từ API)

    @SerializedName("updatedAt")
    private String updatedAtString; // Ngày cập nhật (String từ API)

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getTicketIds() {
        return ticketIds;
    }

    public String getProviderId() {
        return providerId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getBookingExpiresAtString() {
        return bookingExpiresAtString;
    }

    public String getCreatedAtString() {
        return createdAtString;
    }

    public String getUpdatedAtString() {
        return updatedAtString;
    }
}