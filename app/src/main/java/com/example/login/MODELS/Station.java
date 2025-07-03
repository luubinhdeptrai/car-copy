package com.example.login.MODELS;

// Station.java
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Station {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("coordinates") // Thêm annotation cho thuộc tính address
    private Coordinate coordinates; // Thêm thuộc tính coordinates

    @SerializedName("address") // Thêm annotation cho thuộc tính address
    @Expose
    private String address; // Thêm thuộc tính address

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    // Getter cho thuộc tính address
    public String getAddress() {
        return address;
    }

    // Setters (tùy chọn, nếu bạn cần đặt giá trị từ bên ngoài)
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Coordinate getCoordinates() {
        return coordinates;
    }


    public static class Coordinate {
        @SerializedName("lat")
        private double latitude;

        @SerializedName("lng")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}