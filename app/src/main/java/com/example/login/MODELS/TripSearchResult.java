package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

// Ánh xạ kết quả từ GET /api/trips/search
public class TripSearchResult implements Serializable {

    @SerializedName("_id")
    private String id; // Sử dụng 'id' thay vì '_id' để nhất quán với convention Java

    @SerializedName("itinerary")
    private Itinerary itinerary; // Sẽ là Itinerary.java (mới)

    @SerializedName("vehicle")
    private VehicleTripNested vehicle; // Sẽ là VehicleTripNested.java (mới)

    @SerializedName("driver")
    private DriverShort driver; // Sẽ là DriverShort.java (mới)

    @SerializedName("provider")
    private ProviderShort provider; // Sẽ là ProviderShort.java (mới)

    @SerializedName("departureTime")
    private String departureTimeString; // Chuỗi thời gian từ API

    @SerializedName("arrivalTime")
    private String arrivalTimeString; // Chuỗi thời gian từ API

    @SerializedName("status")
    private String status;

    @SerializedName("priceMatrix")
    private List<PriceInfo> priceMatrix; // Sẽ là PriceInfo.java (mới)

    @SerializedName("schedule")
    private List<ScheduleStop> schedule; // Sẽ là ScheduleStop.java (mới)

    @SerializedName("priceForSelectedSegment")
    private Double priceForSelectedSegment; // Giá cho chặng đã chọn


    // Getters và Setters (chỉ cần getters nếu đây là DTO nhận từ API)
    public String getId() {
        return id;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public VehicleTripNested getVehicle() {
        return vehicle;
    }

    public DriverShort getDriver() {
        return driver;
    }

    public ProviderShort getProvider() {
        return provider;
    }

    public String getStatus() {
        return status;
    }

    public List<PriceInfo> getPriceMatrix() {
        return priceMatrix;
    }

    public List<ScheduleStop> getSchedule() {
        return schedule;
    }

    public Double getPriceForSelectedSegment() {
        return priceForSelectedSegment;
    }

    // --- Helpers để parse Date ---
    // Sử dụng SimpleDateFormat với định dạng ISO 8601 UTC
    private static final SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    static {
        apiDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date getDepartureTime() {
        try {
            return apiDateTimeFormat.parse(departureTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Hoặc ném RuntimeException nếu không muốn null
        }
    }

    public Date getArrivalTime() {
        try {
            return apiDateTimeFormat.parse(arrivalTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Hoặc ném RuntimeException nếu không muốn null
        }
    }
}