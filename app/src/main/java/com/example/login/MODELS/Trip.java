// File: com/example/login/MODELS/Trip.java
package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Trip implements Serializable {
    @SerializedName("_id")
    private String id;

    private Route route;
    private Vehicle vehicle;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private String status;

    // Getters
    public String getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}