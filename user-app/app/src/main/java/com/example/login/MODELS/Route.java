package com.example.login.MODELS;

// Route.java
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("originStation")
    @Expose
    private Station originStation;

    @SerializedName("destinationStation")
    @Expose
    private Station destinationStation;

    @SerializedName("distanceKm")
    @Expose
    private int distanceKm;

    @SerializedName("estimatedDurationMin")
    @Expose
    private int estimatedDurationMin;

    // Getters
    public String getId() {
        return id;
    }

    public Station getOriginStation() {
        return originStation;
    }

    public Station getDestinationStation() {
        return destinationStation;
    }

    public int getDistanceKm() {
        return distanceKm;
    }

    public int getEstimatedDurationMin() {
        return estimatedDurationMin;
    }
}