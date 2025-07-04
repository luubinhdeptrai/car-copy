package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Itinerary implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("provider")
    private String provider; // Đây là String ID của Provider

    @SerializedName("baseRoute")
    private Route route; // Sẽ là Route.java (mới)

    @SerializedName("stops")
    private List<StopInfo> stops; // Sẽ là StopInfo.java (mới)

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProvider() {
        return provider;
    }

    public Route getBaseRoute() {
        return route;
    }

    public List<StopInfo> getStops() {
        return stops;
    }
}