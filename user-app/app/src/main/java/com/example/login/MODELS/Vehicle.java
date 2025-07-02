package com.example.login.MODELS;

// Vehicle.java
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("licensePlate")
    @Expose
    private String licensePlate;

    @SerializedName("capacity")
    @Expose
    private int capacity;

    // Getters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getCapacity() {
        return capacity;
    }
}
