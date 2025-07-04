package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LockManySeatsRequest {
    @SerializedName("ticketIds")
    private List<String> ticketIds;

    // THÊM MỚI: Cần gửi tripId, originStopId, destinationStopId cho LockManySeats API
    @SerializedName("tripId")
    private String tripId;
    @SerializedName("originStopId")
    private String originStopId;
    @SerializedName("destinationStopId")
    private String destinationStopId;

    public LockManySeatsRequest(List<String> ticketIds, String tripId, String originStopId, String destinationStopId) {
        this.ticketIds = ticketIds;
        this.tripId = tripId;
        this.originStopId = originStopId;
        this.destinationStopId = destinationStopId;
    }

    public List<String> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(List<String> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getOriginStopId() {
        return originStopId;
    }

    public void setOriginStopId(String originStopId) {
        this.originStopId = originStopId;
    }

    public String getDestinationStopId() {
        return destinationStopId;
    }

    public void setDestinationStopId(String destinationStopId) {
        this.destinationStopId = destinationStopId;
    }
}