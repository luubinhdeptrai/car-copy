package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LockManySeatsRequest {
    @SerializedName("ticketIds")
    private List<String> ticketIds;

    public LockManySeatsRequest(List<String> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public List<String> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(List<String> ticketIds) {
        this.ticketIds = ticketIds;
    }
}
