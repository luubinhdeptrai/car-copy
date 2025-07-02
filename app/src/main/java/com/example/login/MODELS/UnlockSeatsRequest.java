package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UnlockSeatsRequest {

    @SerializedName("ticketIds")
    private List<String> ticketIds;

    public UnlockSeatsRequest(List<String> ticketIds) {
        this.ticketIds = ticketIds;
    }
}