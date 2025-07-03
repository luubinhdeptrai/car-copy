package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class BookingHistoryItem implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("totalPrice")
    private double totalPrice;
    @SerializedName("paymentStatus")
    private String paymentStatus;
    @SerializedName("approvalStatus")
    private String approvalStatus;
    @SerializedName("paymentMethod")
    private String paymentMethod;
    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("review")
    private ReviewInfo review;

    @SerializedName("userInfo")
    private UserInfo userInfo;
    @SerializedName("providerInfo")
    private ProviderInfo providerInfo;
    @SerializedName("tripInfo")
    private TripInfo tripInfo;
    @SerializedName("tickets")
    private List<TicketInfo> tickets;

    // Getters
    public String getId() { return id; }
    public double getTotalPrice() { return totalPrice; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getApprovalStatus() { return approvalStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCreatedAt() { return createdAt; }
    public ReviewInfo getReview() { return review; }
    public UserInfo getUserInfo() { return userInfo; }
    public ProviderInfo getProviderInfo() { return providerInfo; }
    public TripInfo getTripInfo() { return tripInfo; }
    public List<TicketInfo> getTickets() { return tickets; }

    public void setReview(ReviewInfo review) {
        this.review = review;
    }

    public static class ReviewInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        @SerializedName("rating")
        private float rating;
        @SerializedName("comment")
        private String comment;
        public ReviewInfo() {}


        public String getId() { return id; }
        public float getRating() { return rating; }
        public String getComment() { return comment; }

        // << THÊM CÁC HÀM SETTER >>
        public void setId(String id) { this.id = id; }
        public void setRating(float rating) { this.rating = rating; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class UserInfo implements Serializable {
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        @SerializedName("phoneNumber")
        private String phoneNumber;
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
    }

    public static class ProviderInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        @SerializedName("phone")
        private String phone;
        @SerializedName("name")
        private String name;
        public String getName() { return name; }
    }

    public static class TripInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        @SerializedName("origin")
        private String origin;
        @SerializedName("destination")
        private String destination;
        @SerializedName("departureTime")
        private String departureTime;
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public String getDepartureTime() { return departureTime; }
        public String getId() { return id; }
    }

    public static class TicketInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        @SerializedName("seatNumber")
        private String seatNumber;
        @SerializedName("price")
        private double price;
        @SerializedName("accessId")
        private String accessId;
        public String getSeatNumber() { return seatNumber; }
        public double getPrice() { return price; }
        public String getAccessId() { return accessId; }
        public String getId() { return id; }
    }


}