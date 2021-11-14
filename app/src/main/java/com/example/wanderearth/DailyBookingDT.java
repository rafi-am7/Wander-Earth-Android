package com.example.wanderearth;

public class DailyBookingDT {
    private boolean isBooked, isRated;
    private int roomNo;
    private String bookUserId;

    public DailyBookingDT(boolean isBooked, String bookUserId, int roomNo) {
        this.isBooked = isBooked;
        this.bookUserId = bookUserId;
        this.isRated = false;
        this.roomNo = roomNo;
    }

    public DailyBookingDT() {
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public void setBookUserId(String bookUserId) {
        this.bookUserId = bookUserId;
    }


    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public void setRated(boolean rated) {
        this.isRated = rated;
    }

    public String getBookUserId() {
        return bookUserId;
    }

    public boolean getIsBooked() {
        return isBooked;
    }


    public int getRoomNo() {
        return roomNo;
    }

    public boolean getIsRated() {
        return isRated;
    }


}
