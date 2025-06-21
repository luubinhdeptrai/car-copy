package com.example.login.MODELS;

public class Trip {
    private String departure;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private String typeSeat;
    private int emptySeat;
    private double distance;
    private double duration;


    public Trip(String departure, String destination, String departureTime, String arrivalTime, double price, String typeSeat, int emptySeat, double distance, double duration) {
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.typeSeat = typeSeat;
        this.emptySeat = emptySeat;
        this.distance = distance;
        this.duration = duration;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTypeSeat() {
        return typeSeat;
    }

    public void setTypeSeat(String typeSeat) {
        this.typeSeat = typeSeat;
    }

    public int getEmptySeat() {
        return emptySeat;
    }

    public void setEmptySeat(int emptySeat) {
        this.emptySeat = emptySeat;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
