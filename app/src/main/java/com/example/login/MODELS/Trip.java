package com.example.login.MODELS;

import java.io.Serializable;
import java.util.List;

public class Trip implements Serializable {

    private String _id;
    private Route route;
    private Vehicle vehicle;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private String status;

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

    // THÊM MỚI: Thêm getter và setter cho trường mới
    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    // --- Các class con như Route, Vehicle, Station bạn đã có, giữ nguyên ---
    public static class Route implements Serializable {
        private String _id;
        private Station originStation;
        private Station destinationStation;
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
        private String _id;
        private String type;
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
        private String _id;
        private String name;
        private String city;
        private String address; // Ensure 'address' property is here

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
    }
}