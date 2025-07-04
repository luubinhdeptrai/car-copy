package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PriceInfo implements Serializable {
    @SerializedName("originStop")
    private String originStop; // ID của Station

    @SerializedName("destinationStop")
    private String destinationStop; // ID của Station

    @SerializedName("price")
    private double price;

    public String getOriginStop() {
        return originStop;
    }

    public String getDestinationStop() {
        return destinationStop;
    }

    public double getPrice() {
        return price;
    }
}