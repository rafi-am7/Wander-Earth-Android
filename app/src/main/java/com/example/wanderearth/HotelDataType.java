package com.example.wanderearth;

public class HotelDataType {
    private int roomNo, floorNo;
    private double balance, avgRoomPrize, rating;

    public HotelDataType(int roomNo, int floorNo, double balance, double avgRoomPrize, double rating) {
        this.roomNo = roomNo;
        this.floorNo = floorNo;
        this.balance = balance;
        this.avgRoomPrize = avgRoomPrize;
        this.rating = rating;
    }

    public HotelDataType() {
    }

    public int getRoomNo() {
        return roomNo;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public double getBalance() {
        return balance;
    }

    public double getAvgRoomPrize() {
        return avgRoomPrize;
    }

    public double getRating() {
        return rating;
    }
}
