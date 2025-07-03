package com.example.login.MODELS;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Trip implements Serializable {

    @SerializedName("_id")
    private String _id;

    @SerializedName("route")
    private Route route;

    @SerializedName("vehicle")
    private Vehicle vehicle;

    @SerializedName("departureTime")
    private String departureTime;

    @SerializedName("arrivalTime")
    private String arrivalTime;


    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status;

    @SerializedName("provider")
    private Provider provider; // Thêm trường provider để lưu thông tin nhà cung cấp

    // THÊM MỚI: Thêm trường transient để lưu số ghế trống đã tính toán.
    // Dùng transient để thư viện Gson/Moshi bỏ qua khi parse JSON ban đầu.
    // Khởi tạo là -1 để biết rằng dữ liệu chưa được load.
    private transient int availableSeats = -1;

    public String getId() {
        return _id;
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

    public Provider getProvider() {
        return provider;
    }


    // THÊM MỚI: Thêm getter và setter cho trường mới
    public int getAvailableSeats() {
        return availableSeats;
    }


    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    // --- Các class con như Route, Vehicle, Station bạn đã có, giữ nguyên ---
    public static class Route implements Serializable {

        @SerializedName("_id")
        private String _id;


        @SerializedName("originStation")
        private Station originStation;

        @SerializedName("destinationStation")
        private Station destinationStation;

        @SerializedName("distanceKm")
        private int distanceKm;

        public String get_id() { // Added getter for _id, usually good to have
            return _id;
        }

        public Station getOriginStation() {
            return originStation;
        }

        public Station getDestinationStation() {
            return destinationStation;
        }

        public int getDistanceKm() {
            return distanceKm;
        }
    }

    public static class Vehicle implements Serializable {

        @SerializedName("_id")
        private String _id;

        @SerializedName("type")
        private String type;


        @SerializedName("capacity")
        private int capacity;




        public String get_id() { // Added getter for _id, usually good to have
            return _id;
        }

        public String getType() {
            return type;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public static class Station implements Serializable {


        @SerializedName("_id")
        private String _id;

        @SerializedName("name")
        private String name;

        @SerializedName("city")
        private String city;

        @SerializedName("address")
        private String address; // Ensure 'address' property is here

        @SerializedName("coordinates")
        private Coordinate coordinates; // Ensure 'coordinates' property is here


        public String get_id() { // Added getter for _id, usually good to have
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        // << THÊM MỚI: Getter cho thuộc tính address >>
        public String getAddress() {
            return address;
        }

        public Coordinate getCoordinates() {
            return coordinates;
        }


        public static class Coordinate implements Serializable  {
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


    public static class Provider implements Serializable {

        @SerializedName("_id")
        private String _id;

        @SerializedName("name")
        private String name;

        @SerializedName("phone")
        private String phone;

        public String getId() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }
    }
}