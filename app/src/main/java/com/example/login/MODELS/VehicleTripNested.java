package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class VehicleTripNested implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("licensePlate")
    private String licensePlate;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}