package com.example.firebnb;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.Object;
import java.util.stream.Collectors;

public class ReducerThread extends Thread {
    private  int i;
    private Socket connection;
    private Map<List<String>,List<Room>> map;
    private Map<List<String>,List<Room>> tempMap;
    private TCPObject obj;
    private List<Room> results=new ArrayList<>();


    public ReducerThread(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        System.out.println("I am now in ReducerThread");
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());

            int a = in.readInt();

            if(a == 10){
                System.out.println("Taskid10 => kleinoo...");

                synchronized (Reducer.mapnum){
                    Reducer.mapnum = 0;
                }
            }
            else{
                obj = (TCPObject) in.readObject();

                ConfirmationMessage confirmation = new ConfirmationMessage("Request received and processed.");
                out.writeObject(confirmation);
                out.flush();

                tempMap = obj.getMap();

                synchronized (Reducer.mapnum){
                    Reducer.mapnum++;
                }

                map = (Map<List<String>, List<Room>>) tempMap;
                synchronized (map){
                    fillFinalmap();
                }

                synchronized (Reducer.mapnum) {
                    if (Reducer.mapnum == 1) {
                    /*synchronized (Reducer.finalmap) {
                        Reducer.finalmap = new HashMap<>();
                    }*/
                    }
                }

                System.out.println(Reducer.mapnum);
                synchronized (Reducer.mapnum) {
                    if (Reducer.mapnum >= 3) {
                        //Reducer.mapnum=0;
                        Reducer.mapnum = 0;

                        sendtoMaster();

                    }
                }

            }

        } catch (IOException | ClassNotFoundException e) {
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

    }

    private synchronized void sendtoMaster() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            obj.setMap(Reducer.finalmap);
            obj.setResults(results);
            obj.setTaskId(4);

            System.out.println("Sending object to the master...");
            out.writeObject(obj); // Sending the Object instance
            out.flush();

            ConfirmationMessage confirmation = (ConfirmationMessage) in.readObject();
            System.out.println("Server response: " + confirmation.getMessage());

            Reducer.finalmap = new HashMap<List<String>, List<Room>>();
            synchronized (Reducer.mapnum){
                Reducer.mapnum=0;
            }
            //obj.setMap(Reducer.finalmap);
            //obj.setDates(null);

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (requestSocket != null) requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }




    private synchronized Map<List<String>, List<Room>> reduceExtra(String s, String c, Map<String, List<Room>> finalmap) {

        if (finalmap.isEmpty()) {
            return null; // Return the empty map if there are no entries
        }
        System.out.println(c);
        String key = finalmap.keySet().iterator().next();

        // Get the value associated with the key
        List<Room> value = finalmap.get(key);

        switch (c) {
            case "roomName":
                synchronized (value) {
                    if (value != null) {
                        Iterator<Room> iterator = value.iterator();
                        while (iterator.hasNext()) {
                            Room room = iterator.next();
                            if (!room.getRoomName().equals(s)) {
                                synchronized (iterator){
                                    iterator.remove();
                                }

                            }
                        }
                    }
                    synchronized (finalmap){
                        finalmap.put(key, value);
                    }

                    return map;
                }
            case "noOfPersons":
                synchronized (value) {
                    if (value != null) {
                        List<Room> filteredRooms = new ArrayList<>();
                        for (Room room : value) {
                            if (String.valueOf(room.getNoOfPersons()).equals(s)) {
                                synchronized (filteredRooms){
                                    filteredRooms.add(room);
                                }
                            }
                        }
                        synchronized (finalmap){
                            finalmap.put(key, filteredRooms);
                        }
                    }
                    return map;
                }
            case "stars":
                synchronized (value) {
                    if (value != null) {
                        List<Room> filteredRooms = new ArrayList<>();
                        for (Room room : value) {
                            if (String.valueOf(room.getStars()).equals(s)) {
                                synchronized (filteredRooms){
                                    filteredRooms.add(room);
                                }
                            }
                        }
                        synchronized (finalmap){
                            finalmap.put(key, filteredRooms);
                        }
                    }
                    return map;
                }
            case "noOfReviews":
                synchronized (value) {
                    if (value != null) {
                        List<Room> filteredRooms = new ArrayList<>();
                        for (Room room : value) {
                            if (String.valueOf(room.getNoOfReviews()).equals(s)) {
                                synchronized (filteredRooms){
                                    filteredRooms.add(room);
                                }
                            }
                        }
                        synchronized (finalmap){
                            finalmap.put(key, filteredRooms);
                        }
                    }
                    return map;
                }
            case "datesAvailable":

                synchronized (value) {
                    if (value != null) {
                        List<Room> filteredRooms = new ArrayList<>();
                        for (Room room : value) {
                            if (String.valueOf(room.getNoOfReviews()).equals(s)) {
                                synchronized (filteredRooms){
                                    filteredRooms.add(room);
                                }

                            }
                        }
                        synchronized (finalmap){
                            finalmap.put(key, filteredRooms);
                        }

                    }
                    return map;
                }
            case "datesBooked":
                synchronized (value) {
                    if (value != null) {
                        Iterator<Room> iterator = value.iterator();
                        while (iterator.hasNext()) {
                            Room room = iterator.next();
                            boolean flag = false;
                            for (String date : room.getDatesBooked()) {
                                if (date.equals(s)) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                synchronized (iterator){
                                    iterator.remove();
                                }

                            }
                        }
                    }
                    finalmap.put(key, value);
                    return map;
                }
            case "owner":
                synchronized (value) {
                    if (value != null) {
                        Iterator<Room> iterator = value.iterator();
                        while (iterator.hasNext()) {
                            Room room = iterator.next();
                            if (!room.getOwner().equals(s)) {
                                synchronized (iterator){
                                    iterator.remove();
                                }

                            }
                        }
                    }
                    synchronized (finalmap){
                        finalmap.put(key, value);
                    }

                    return map;
                }
            case "area":
                synchronized (value) {
                    if (value != null) {
                        List<Room> filteredRooms = new ArrayList<>();
                        for (Room room : value) {
                            if (room.getArea().equals(s)) {
                                synchronized (filteredRooms){
                                    filteredRooms.add(room);
                                }

                            }
                        }
                        synchronized (finalmap){
                            finalmap.put(key, filteredRooms);
                        }

                    }
                    return map;
                }
            default:
                return null;
        }

    }


    private synchronized void fillFinalmap(){


        Map<List<String>, List<Room>> finalMap = obj.getMap();


        synchronized (results) {
            for (List<Room> roomList : finalMap.values()) {
                if (roomList != null) {
                    results.addAll(roomList);
                }
            }
        }


        if (Reducer.finalmap.isEmpty() ){
            synchronized (Reducer.finalmap){
                Reducer.finalmap= finalMap;
            }
        } else {

            if (finalMap.get(obj.getListMapID()) != null && !(finalMap.get(obj.getListMapID()).isEmpty())) {
                List<Room> values = Reducer.finalmap.get(obj.getListMapID());

                if (values == null) {
                    values = new ArrayList<>(); // Initialize the list if it's null
                    synchronized (Reducer.finalmap) {
                        Reducer.finalmap.put(obj.getListMapID(), values);
                    }
                }
                System.out.println("  1 . "+ Reducer.finalmap.toString());

                for (Room room : finalMap.get(obj.getListMapID())) {
                    boolean roomExists = false;
                    synchronized (values){
                        for (Room existingRoom : values) {
                            if (room.getRoomName().equals(existingRoom.getRoomName())) {
                                roomExists = true;
                                break;
                            }
                        }
                        if (!roomExists) {
                            values.add(room);
                        }

                    }

                }

                synchronized (Reducer.finalmap){
                    System.out.println("  2 . "+ Reducer.finalmap.toString());
                    Reducer.finalmap.put(obj.getListMapID(), values);
                }

            }

        }
        System.out.println(Reducer.finalmap.toString());

    }


}