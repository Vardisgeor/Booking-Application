package com.example.firebnb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private String roomName;
    private int noOfPersons;
    private String area;
    private StarsGrade stars;
    private int noOfReviews;
    private String roomImage;
    private List<String> datesAvailable;
    private List<String> datesBooked; // New field for dates booked
    private String owner; // Existing field for the owner's name
    private List<Booking> bookings = new ArrayList<>();;


    public Room(){}

    public Room(String roomName, int noOfPersons, String area, StarsGrade stars, int noOfReviews, String roomImage, List<String> datesAvailable, List<String> datesBooked, String owner,List<Booking> bookings) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.datesAvailable = datesAvailable;
        this.datesBooked = datesBooked; // Initialize the datesBooked field
        this.owner = owner; // Initialize the owner field
        this.bookings = bookings;

    }

    // Getters and Setters
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfPersons(int noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public StarsGrade getStars() {
        return stars;
    }

    public void setStars(StarsGrade stars) {
        this.stars = stars;
    }


    public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public List<String> getDatesAvailable() {
        return datesAvailable;
    }

    public void setDatesAvailable(List<String> datesAvailable) {
        this.datesAvailable = datesAvailable;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getDatesBooked() {
        return datesBooked;
    }

    public void setDatesBooked(List<String> datesBooked) {
        this.datesBooked = datesBooked;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    // Updated print method to include datesBooked
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Room{")
                .append("roomName='").append(roomName).append("', ")
                .append("noOfPersons=").append(noOfPersons).append(", ")
                .append("area='").append(area).append("', ")
                .append("stars=").append(stars.getStars()).append(", ") // Use StarsGrade getter
                .append("averageStars=").append(stars.getAverage()).append(", ") // Use StarsGrade getter
                .append("noOfReviews=").append(noOfReviews).append(", ")
                .append("roomImage='").append(roomImage).append("', ")
                .append("datesAvailable=").append(datesAvailable).append(", ")
                .append("datesBooked=").append(datesBooked).append(", ")
                .append("owner='").append(owner).append("', ")
                .append("bookings=[");

        for (Booking booking : bookings) {
            sb.append("{username='").append(booking.getUsername()).append("', dates=[");
            for (String date : booking.getDates()) {
                sb.append(date).append(", ");
            }
            // Remove the last comma and space
            if (!bookings.isEmpty()) {
                sb.setLength(sb.length() - 2); // Remove the last comma and space
            }
            sb.append("]}, ");
        }

        // Remove the last comma and space
        if (!bookings.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and space
        }
        sb.append("]}");

        return sb.toString();
    }

    public void printBookings() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Bookings for room " + roomName + ":");
        for (Booking booking : bookings) {
            System.out.println("Username: " + booking.getUsername());
            System.out.println("Dates: " + booking.getDates());
            System.out.println("-----------------------------");
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
    }
}