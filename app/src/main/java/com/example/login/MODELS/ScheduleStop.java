package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleStop implements Serializable {
    @SerializedName("station")
    private String station; // ID của Station

    @SerializedName("estimatedArrivalTime")
    private String estimatedArrivalTimeString; // Chuỗi thời gian từ API

    @SerializedName("estimatedDepartureTime")
    private String estimatedDepartureTimeString; // Chuỗi thời gian từ API

    public String getStation() {
        return station;
    }

    // --- Helpers để parse Date ---
    private static final SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    static {
        apiDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date getEstimatedArrivalTime() {
        try {
            return apiDateTimeFormat.parse(estimatedArrivalTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getEstimatedDepartureTime() {
        try {
            return apiDateTimeFormat.parse(estimatedDepartureTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}