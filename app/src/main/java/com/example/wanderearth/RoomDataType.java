package com.example.wanderearth;

public class RoomDataType {
    private int noOfRatings;
    private double roomPrize, rating;

    public RoomDataType(double roomPrize, int noOfRatings, double rating) {
        this.roomPrize = roomPrize;
        this.noOfRatings = noOfRatings;
        this.rating = rating;
    }

    public RoomDataType() {
    }

    public double getRating() {
        return rating;
    }

    public int getNoOfRatings() {
        return noOfRatings;
    }

    public double getRoomPrize() {
        return roomPrize;
    }
}
