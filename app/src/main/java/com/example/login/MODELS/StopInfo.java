package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class StopInfo implements Serializable {
    @SerializedName("station")
    private Station station; // Sẽ là Station.java (mới)

    @SerializedName("order")
    private int order;

    public Station getStation() {
        return station;
    }

    public int getOrder() {
        return order;
    }
}