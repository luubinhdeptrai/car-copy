package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Coordinates implements Serializable {
    @SerializedName("lat")
    private Double latitude; // Có thể null
    @SerializedName("lng")
    private Double longitude; // Có thể null

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}