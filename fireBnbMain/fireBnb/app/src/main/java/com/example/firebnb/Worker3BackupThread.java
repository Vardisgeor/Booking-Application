package com.example.firebnb;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;



public class Worker3BackupThread extends Thread {
    private Socket connection;
    private Worker3Backup worker; // Add this line to store a reference to the Worker instance
    private TCPObject obj;

    public Worker3BackupThread(Socket connection, Worker3Backup worker) { // Modify the constructor
        this.connection = connection;
        this.worker = worker; // Initialize the worker reference
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        System.out.println("I am now in WorkerThread");
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());

            obj = (TCPObject) in.readObject();

            ConfirmationMessage confirmation = new ConfirmationMessage("Request received and processed.");
            out.writeObject(confirmation);
            out.flush();


            if (obj.getTaskId() == 1){
                readRooms();

                String filePath = "/Users/vardisgeorgilas/Desktop/fireBnbMain/fireBnb/app/src/main/java/com/example/firebnb/room4322.json";

                Path path = Paths.get(filePath);

                if (Files.exists(path)) {
                    Worker3Backup.rooms = Worker3Backup.readRoomsFromJson(filePath);
                    System.out.println("JUST READ ROOMS");

                    for (Room room : Worker3Backup.rooms){
                        System.out.println(room.print());
                    }
                    // Proceed with your logic here
                } else {
                    System.out.println("File does not exist: " + filePath);
                    // Handle the case where the file does not exist
                }

            }
            else if (obj.getTaskId() == 2){
                addDates();
                Worker3Backup.saveRoomsToJson(worker.connection.getLocalPort());
            }
            else if (obj.getTaskId() == 3){
                printBookings();
            }else if (obj.getTaskId() == 5){
                addBooking();
                Worker3Backup.saveRoomsToJson(worker.connection.getLocalPort());
            }
            else if (obj.getTaskId() == 6){
                addGrade();
                Worker3Backup.saveRoomsToJson(worker.connection.getLocalPort());
            }

            //out.flush();
            System.out.println("PRINT BOOKING CLOSED AND NOW THE  THREAD..");


        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (connection != null) connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void addGrade() {

        for (Room room : Worker3Backup.rooms){
            room.print();
        }

        List<Booking> newBookings = new ArrayList<>();

        // Find the room in the rooms list that matches roomName
        Room roomToUpdate = null;
        for (Room room : Worker3Backup.rooms) {
            if (room.getRoomName().equals(obj.getroomNameToBook())) {
                roomToUpdate = room;
                Worker3Backup.rooms.remove(room);
                break;
            }
        }

        // If the room is found, update its datesAvailable list
        if (roomToUpdate != null) {

            System.out.println("Room found with roomName: " + obj.getroomNameToBook());

            // Increment the number of reviews
            roomToUpdate.setNoOfReviews(roomToUpdate.getNoOfReviews() + 1);
            // Add the new star rating to the list of stars
            roomToUpdate.getStars().getStars().add(obj.getNumOfStarsToGrade());

            // Calculate the new average of the stars
            double totalStars = 0;
            for (int star : roomToUpdate.getStars().getStars()) {
                totalStars += star;
            }
            double averageStars = totalStars / roomToUpdate.getStars().getStars().size();

            // Update the StarsGrade object of the roomToUpdate with the new average
            roomToUpdate.getStars().setAverage((int)averageStars);

            Worker3Backup.rooms.add(roomToUpdate);

            for (Room room : Worker3Backup.rooms){
                room.print();
            }

        } else {
            System.out.println("Room not found with roomName: " + obj.getroomNameToBook());
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void addBooking() {

        for (Room room : Worker3Backup.rooms){
            room.print();
        }

        List<Booking> newBookings = new ArrayList<>();

        // Find the room in the rooms list that matches roomName
        Room roomToUpdate = null;
        for (Room room : Worker3Backup.rooms) {
            if (room.getRoomName().equals(obj.getroomNameToBook())) {
                roomToUpdate = room;
                Worker3Backup.rooms.remove(room);
                break;
            }
        }

        List<String> newDatesAvailable = new ArrayList<>();

        boolean b = false;

        // If the room is found, update its datesAvailable list
        if (roomToUpdate != null) {

            System.out.println("Room found with roomName: " + obj.getroomNameToBook());

            for (String date : roomToUpdate.getDatesAvailable()){
                for (String date2 : obj.getBooking().getDates()){
                    System.out.println("date = " + date);
                    System.out.println("date2 = " + date2);
                    System.out.println("date.equals(date2) = " + date.equals(date2));
                    if( date.equals(date2)){
                        b = true;
                        break;
                    }
                }

                if(b == false){
                    newDatesAvailable.add(date);
                }
                else{
                    b = false;
                }
            }

            roomToUpdate.setDatesAvailable(newDatesAvailable);

            newBookings = roomToUpdate.getBookings();
            newBookings.add(obj.getBooking());
            roomToUpdate.setBookings(newBookings);

            Worker3Backup.rooms.add(roomToUpdate);

            for (Room room : Worker3Backup.rooms){
                room.print();
            }


        } else {
            System.out.println("Room not found with roomName: " + obj.getroomNameToBook());
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void printBookings() {

        //System.out.println(obj.getListCategory());
        obj.setMap(null);

        ObjectInputStream in2 = null;
        ObjectOutputStream out2 = null;
        Socket requestSocket = null;
        try {
            requestSocket = new Socket("192.168.89.156", 4325);
            out2 = new ObjectOutputStream(requestSocket.getOutputStream());
            in2 = new ObjectInputStream(requestSocket.getInputStream());


            out2.writeInt(1);
            out2.flush();

            obj.setMap(reduce(obj.getListMapID(),mapping(obj.getListCategory(), Worker3Backup.rooms)));
            out2.writeObject(obj);
            out2.flush();

            ConfirmationMessage confirmation = (ConfirmationMessage) in2.readObject();
            System.out.println("Server response: " + confirmation.getMessage());


            System.out.println("Got notified..");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out2 != null) out2.close();
                if (in2 != null) in2.close();
                if (requestSocket != null) requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        System.out.println("PRINT BOOKING CLOSED..");


    }


    private Map<List<String>, List<Room>> mapping(List<String> c, List<Room> rooms) {

        Map<List<String>, List<Room>> map = new HashMap<>();;

        System.out.println("i am in mapping!!  "+c);

        for (Room room : rooms) {
            List<String> compositeKeyParts = new ArrayList<>();
            for (String category : c) {
                switch (category) {
                    case "roomName":
                        compositeKeyParts.add(room.getRoomName());
                        break;
                    case "noOfPersons":
                        compositeKeyParts.add(String.valueOf(room.getNoOfPersons()));
                        break;
                    case "stars":
                        compositeKeyParts.add(String.valueOf(room.getStars()));
                        break;
                    case "noOfReviews":
                        compositeKeyParts.add(String.valueOf(room.getNoOfReviews()));
                        break;
                    case "datesAvailable":
                        if(!compositeKeyParts.containsAll(room.getDatesAvailable())) {
                            compositeKeyParts.addAll(room.getDatesAvailable());
                        }
                        break;
                    case "datesBooked":
                        compositeKeyParts.addAll(room.getDatesBooked());
                        break;
                    case "owner":
                        compositeKeyParts.add(room.getOwner());
                        break;
                    case "area":
                        compositeKeyParts.add(room.getArea());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid category: " + category);
                }
            }
            if (map.containsKey(compositeKeyParts)) {
                map.get(compositeKeyParts).add(room);
            } else {
                map.put(compositeKeyParts, new ArrayList<>(Collections.singletonList(room)));
                System.out.println(compositeKeyParts+" jjjjj "+new ArrayList<>(Collections.singletonList(room)));
            }
        }
        System.out.println(map);
        return map;

    }

    private synchronized Map<List<String>, List<Room>> reduce(List<String> mapID, Map<List<String>, List<Room>> map) {

        Map<List<String>, List<Room>> filteredMap = map.entrySet().stream()
                .filter(entry -> entry.getKey().equals(mapID))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Room> list = new ArrayList<>();

        for (Map.Entry<List<String>, List<Room>> entry : map.entrySet()) {


            if (entry.getKey().containsAll(mapID)){
                list.addAll(entry.getValue());
            }
            filteredMap.put(mapID,list);
        }

        System.out.println("THis is the filtered map: "+filteredMap);
        return filteredMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void addDates() {

        for (Room room : Worker3Backup.rooms){
            room.print();
        }

        Room updatedRoom = obj.getRoom();
        String owner = updatedRoom.getOwner();
        String roomName = updatedRoom.getRoomName();
        List<String> newDatesAvailable = updatedRoom.getDatesAvailable();

        // Find the room in the rooms list that matches the owner and roomName
        Room roomToUpdate = null;
        for (Room room : Worker3Backup.rooms) {
            if (room.getOwner().equals(owner) && room.getRoomName().equals(roomName)) {
                roomToUpdate = room;
                Worker3Backup.rooms.remove(room);
                break;
            }
        }

        // If the room is found, update its datesAvailable list
        if (roomToUpdate != null) {

            System.out.println("Room found for owner: " + owner + " and roomName: " + roomName);

            System.out.println("Room found for owner: " + owner + " and roomName: " + roomName);
            List<String> currentDatesAvailable = roomToUpdate.getDatesAvailable();
            for (String newDate : newDatesAvailable) {
                if (!currentDatesAvailable.contains(newDate)) {
                    currentDatesAvailable.add(newDate);
                }
            }
            // Update the room object in the rooms list
            roomToUpdate.setDatesAvailable(currentDatesAvailable);

            Worker3Backup.rooms.add(roomToUpdate);

            for (Room room : Worker3Backup.rooms){
                System.out.println(room.print());
            }


        } else {
            System.out.println("Room not found for owner: " + owner + " and roomName: " + roomName);
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void readRooms() throws InterruptedException {

        synchronized (Worker3Backup.rooms){
            Worker3Backup.rooms.add(obj.getRoom());
            Worker3Backup.rooms.notifyAll();
            System.out.println("notifing rooms.add(obj.getRoom());");
            System.out.println("worker.connection.getLocalPort() : "+ worker.connection.getLocalPort() + "Rooms.size = " + Worker3Backup.rooms.size());
        }

        synchronized (Worker3Backup.rooms){
            System.out.println("waiting for saveRoomsToJson(worker.connection.getLocalPort());");
            Worker3Backup.rooms.wait();
            Worker3Backup.saveRoomsToJson(worker.connection.getLocalPort()); // Call saveRoomsToJson to update the JSON file
        }


    }

}

