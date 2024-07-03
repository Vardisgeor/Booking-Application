package com.example.firebnb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TCPObject implements Serializable {
    private Booking booking;
    private Room room;
    private int taskId;
    private String category;
    private Map<List<String>, List<Room>> map;
    private String mapID;
    private List<String> listMapID;
    private List<Room> results;
    private List<String> listCategory;
    private List<String> dates;
    private List<Booking> bookings = new ArrayList<>();
    private String roomNameToBook;
    private String roomNameToGrade; // New field for roomNameToGrade
    private int numOfStarsToGrade; // Assuming this field was added for the previous constructor

    //private int dateID;

    public TCPObject(Room room, int taskId) {
        this.room = room;
        this.taskId = taskId;
    }

    public TCPObject(String roomNameToGrade, int numOfStarsToGrade, int taskId) {
        this.roomNameToGrade = roomNameToGrade; // Initialize the new field
        this.numOfStarsToGrade = numOfStarsToGrade;
        this.taskId = taskId;
    }

    public TCPObject(List<Booking> bookings){
        this.bookings = bookings;
    }
    public TCPObject(int taskId) {
        this.taskId = taskId;
    }

    public TCPObject(String roomNameToBook, Booking booking, int taskId) {
        this.roomNameToBook = roomNameToBook;
        this.booking = booking;
        this.taskId = taskId;
    }


    public TCPObject(List<String> mapID, List<String> category, int taskId ){
        this.listMapID = mapID;
        this.listCategory = category;
        //if(mapID.size()!=0) {
        this.category = category.get(0);
        this.mapID = mapID.get(0);
        //}
        //this.dateID = dateID;
        this.taskId = taskId;
        this.listCategory.remove(category);
        this.listMapID.remove(mapID);

    }

    public Booking getBooking() {
        return booking;
    }

    // Setter for booking
    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public void setroomNameToBook(String roomNameToBook){
        this.roomNameToBook = roomNameToBook;
    }

    public List<Room> getResults() {
        return results;
    }

    public void setResults(List<Room> results) {
        this.results = results;
    }

    public String getroomNameToBook(){ return roomNameToBook;}

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    public List<Booking> getBookings() {
        return bookings;
    }

    public int getNumOfStarsToGrade() {
        return numOfStarsToGrade;
    }

    public void setNumOfStarsToGrade(int numOfStarsToGrade) {
        this.numOfStarsToGrade = numOfStarsToGrade;
    }

    // Getter and Setter for roomNameToGrade
    public String getRoomNameToGrade() {
        return roomNameToGrade;
    }

    public void setRoomNameToGrade(String roomNameToGrade) {
        this.roomNameToGrade = roomNameToGrade;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<String> getDates() {
        return dates;
    }

    public List<String> getListCategory() {
        return listCategory;
    }

    public List<String> getListMapID() {
        return listMapID;
    }

    public String getMapID() {
        return mapID;
    }

    public void setMapID(String mapID) {
        this.mapID = mapID;
    }

    public Map<List<String>, List<Room>> getMap() {
        return map;
    }

    public void setMap(Map<List<String>, List<Room>> map) {
        /*if(this.map == null) {
            this.map = map;
        }else {
            this.map.putAll(map);
        }*/
        this.map = map;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // Getters and Setters for taskId
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
