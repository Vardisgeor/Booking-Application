package com.example.firebnb;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {
    private String username;
    private List<String> dates;


    public Booking(String username, List<String> dates) {
        this.username = username;
        this.dates = dates;
    }

    public Booking() {
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    // Method to print the Booking object
    public void printBooking() {
        System.out.println("Username: " + username);
        System.out.println("Dates: " + dates.toString());
    }
}

