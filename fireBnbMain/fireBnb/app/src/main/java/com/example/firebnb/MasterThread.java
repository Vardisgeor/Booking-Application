package com.example.firebnb;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MasterThread extends Thread {
    private  int i;
    private Socket connection;
    private int portNum = 4322;
    private TCPObject obj;
    private Map<List<String>, List<Room>> retrievedList;

    public MasterThread(Socket connection) {
        this.connection = connection;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        System.out.println("I am now in MasterThread");
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        ObjectOutputStream out2 = null;
        try {
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());


            obj = (TCPObject) in.readObject();

            synchronized (Master.connectionsList){
                Master.connectionsList.add(out);
                System.out.println("SIZE of CONNECTION LIST = " + Master.connectionsList.size());
            }

            if (obj.getTaskId() == 9){

                if (Master.InPrintBooking == true){
                    Master.InPrintBooking = false;
                    ConfirmationMessage confirmation = new ConfirmationMessage("An Error occured, please try again.");
                    out.writeObject(confirmation);
                    out.flush();

                    System.out.println("in taskid9");


                    out2 = (Master.connectionsList.get(Master.connectionsList.size()-2));
                    out2.writeInt(9);
                    out2.flush();

                    System.out.println("Master.InPrintBooking == true...");

                }

                System.out.println("Master.InPrintBooking == false...");

                Master.connectionsList.clear();
                synchronized (Master.var){
                    Master.var.set(0);
                }

                synchronized (Master.var){
                    System.out.println("notify to END the waiting thread in taskid9...");
                    Master.var.notify();
                }



            }
            else{

                if (obj.getTaskId() == 7){
                    obj.setTaskId(3);

                    Master.TASKID7 = true;

                    Master.datesInspectBookingForFilter = (List<String>) in.readObject();

                    System.out.println(Master.datesInspectBookingForFilter.toString());

                    System.out.println("TASKID ====== " + obj.getTaskId());

                }


                if (obj.getTaskId() == 4){

                    ConfirmationMessage confirmation = new ConfirmationMessage("Request received and processed.");
                    out.writeObject(confirmation);
                    out.flush();

                    obj.setTaskId(3);
                }

                System.out.println("Master.var1 = " + Master.var.get());

                if (obj.getTaskId()!= 3){
                    ConfirmationMessage confirmation = new ConfirmationMessage("Request received and processed.");
                    out.writeObject(confirmation);
                    out.flush();
                }
                else if (obj.getTaskId() == 3){

                    Master.InPrintBooking = true;

                    System.out.println("Master.var2 = " + Master.var.get());
                    synchronized (Master.var){
                        Master.var.incrementAndGet();
                    }
                    System.out.println("Master.var3 = " + Master.var.get());
                    if (Master.var.get() == 1){
                        printBookings();

                        synchronized (Master.var){
                            System.out.println("NOW WAIT AFTER PRINTBOOKING...");
                            Master.var.wait();
                        }

                        //while(true){

                        //}
                    }
                    else if(Master.var.get() == 2){
                        Master.var.set(0);
                        System.out.println("Bghka apo whiletrue...");

                        //System.out.println("Final map = "+obj.getMap().toString());

                        //List<Room> retrievedList = obj.getMap().get(obj.getMapID());

                        if(Master.TASKID7){

                            System.out.println("MPHKA TASKID7 KAI AYTOS EINAI O MAP..."+obj.getMap().toString());

                            Master.TASKID7 = false;

                            List<List<String>> bookingCounts = countBookingsByArea(obj.getMap(), Master.datesInspectBookingForFilter);

                            System.out.println("Gyrisa apo method.. size = " + bookingCounts.size());

                            for (List<String> info : bookingCounts) {
                                System.out.println(info.get(0) + ": " + info.get(1));
                            }

                            System.out.println("tora steilto..");

                            System.out.println("SIZE of CONNECTION LIST AKRIBOS PRIN TO STEILEI = " + Master.connectionsList.size());

                            out2 = (Master.connectionsList.get(Master.connectionsList.size()-2));

                            out2.writeInt(7);
                            out2.flush();

                            out2.writeObject(bookingCounts);
                            out2.flush();



                        }else{

                            List<String> base64Images = new ArrayList<>();

                            Map<String, String> roomImageMap = new HashMap<>();
                            for (List<Room> rooms : obj.getMap().values()) {
                                for (Room room : rooms) {
                                    String roomId = room.getRoomName(); // Get the unique identifier for the room
                                    String imagePath = room.getRoomImage();

                                    try (FileInputStream fis = new FileInputStream(imagePath)) {
                                        byte[] imageData = new byte[fis.available()];
                                        int bytesRead = fis.read(imageData);
                                        if (bytesRead < 0) throw new IOException("Error reading file");

                                        // Convert byte array to Base64 string
                                        String base64Image = Base64.getEncoder().encodeToString(imageData);
                                        roomImageMap.put(roomId, base64Image); // Store the image in the map with the room's ID as the key
                                    } catch (IOException e) {
                                        System.out.println("Error reading PNG file: " + e.getMessage());
                                    }
                                }
                            }



                            System.out.println("MAP TO SEND BACK TO CLIENT..."+obj.getMap().toString());

                            System.out.println("SIZE of CONNECTION LIST AKRIBOS PRIN TO STEILEI = " + Master.connectionsList.size());

                            out2 = (Master.connectionsList.get(Master.connectionsList.size()-2));

                            out2.writeInt(3);
                            out2.flush();

                            out2.writeObject(roomImageMap);
                            out2.flush();

                            out2.writeObject(obj.getMap());
                            out2.flush();
                            System.out.println("OK TO STEILA..."+obj.getMap().toString());

                            Master.connectionsList.clear();
                            synchronized (Master.var){
                                Master.var.set(0);
                            }

                            synchronized (Master.var){
                                System.out.println("notify to END the waiting thread...");
                                Master.var.notify();
                            }

                        }

                    }
                    Master.InPrintBooking = false;

                }


                if (obj.getTaskId() == 0){
                    countWorker();
                }
                if (obj.getTaskId() == 1){
                    readRooms();
                }
                else if (obj.getTaskId() == 2){
                    addDates();
                }
                else if (obj.getTaskId() == 5){
                    addBooking();
                }
                else if (obj.getTaskId() == 6){
                    addGrade();
                }

                System.out.println("PAO NA KLEISOOO...");

            }



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in!= null) in.close();
                if (out!= null) out.close();
                if (out2!= null) out2.close();
                if (connection!= null) connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static List<List<String>> countBookingsByArea(Map<List<String>, List<Room>> roomsMap, List<String> dateRange) {
        List<List<String>> areaBookingCounts = new ArrayList<>();


        // Iterate through each list of rooms
        for (List<Room> rooms : roomsMap.values()) {

            List<String> Areas = new ArrayList<>();

            for (Room room : rooms) {
                String area = room.getArea();

                // Check if the area is not already in the Areas list
                if (!Areas.contains(area)) {
                    // Add the area to the Areas list
                    Areas.add(area);
                }
            }

            for (String area : Areas){

                int i = 0;

                for (Room room : rooms) {

                    if (area.equals(room.getArea())) {

                        List<String> datesBooked = room.getDatesBooked();

                        // Check if the room's booked dates overlap with the date range
                        boolean overlaps = false;
                        for (String date : datesBooked) {
                            for (String rangeDate : dateRange) {
                                if (date.equals(rangeDate)) {
                                    overlaps = true;
                                    i++;
                                    break;
                                }
                            }
                            if (overlaps) break;
                        }

                    }
                }

                List<String> newList = new ArrayList<>(List.of(area, String.valueOf(i)));

                areaBookingCounts.add(newList);

            }
        }

        return areaBookingCounts;
    }

    private void addGrade() {

        System.out.println("I am now in addBooking");

        ObjectInputStream in2 = null;
        ObjectOutputStream out2 = null;
        Socket requestSocket = null;

        int port= 4321;

        for(int i=1;i<=3;i++) {

            port++;

            try {
                requestSocket = new Socket(Master.ips.get(i-1), port);
                out2 = new ObjectOutputStream(requestSocket.getOutputStream());
                in2 = new ObjectInputStream(requestSocket.getInputStream());
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
        }

    }

    private void addBooking() {

        System.out.println("I am now in addBooking");

        ObjectInputStream in2 = null;
        ObjectOutputStream out2 = null;
        Socket requestSocket = null;

        int port= 4321;

        for(int i=1;i<=3;i++) {

            port++;

            try {
                requestSocket = new Socket(Master.ips.get(i-1), port);
                out2 = new ObjectOutputStream(requestSocket.getOutputStream());
                in2 = new ObjectInputStream(requestSocket.getInputStream());
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
        }

    }

    private void printFromReducer() throws IOException {

        System.out.println("print from Reducer in master...");

        System.out.println("Final map = "+obj.getMap().toString());

        List<Room> retrievedList = obj.getMap().get(obj.getMapID());

        for (Room room : retrievedList){
            room.printBookings();
        }

        for (Room room : retrievedList){
            System.out.println(room.print());
        }

        ObjectOutputStream out = new ObjectOutputStream(Master.connectionsList.get(0));
        out.writeObject(retrievedList);
        out.flush();
    }

    private void countWorker() {
        synchronized (Master.WorkerNum){
            Master.WorkerNum.incrementAndGet();
            System.out.println("WorkerNum in countWorker = " + Master.WorkerNum.get());
        }
    }

    private void printBookings() {
        ObjectInputStream in2 = null;
        ObjectOutputStream out2 = null;
        Socket requestSocket = null;

        int port= 4321;

        for(int i = 1; i <= Master.WorkerNum.get(); i++) {

            port++;

            System.out.println(Master.ips.get(i-1));
            try {
                requestSocket = new Socket(Master.ips.get(i-1), port);
                out2 = new ObjectOutputStream(requestSocket.getOutputStream());
                in2 = new ObjectInputStream(requestSocket.getInputStream());
                out2.writeObject(obj);
                out2.flush();

                ConfirmationMessage confirmation = (ConfirmationMessage) in2.readObject();
                System.out.println("Server response: " + confirmation.getMessage());

                System.out.println("Got notified..");
            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (SocketException SE){
                System.err.println("I am handling thiw error differently!");
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
        }

    }

    private void addDates() {
        System.out.println("I am now in addDates");

        ObjectInputStream in2 = null;
        ObjectOutputStream out2 = null;
        Socket requestSocket = null;

        int port= 4321;

        for(int i=1;i<=3;i++) {

            port++;

            try {
                requestSocket = new Socket(Master.ips.get(i-1), port);
                out2 = new ObjectOutputStream(requestSocket.getOutputStream());
                in2 = new ObjectInputStream(requestSocket.getInputStream());
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
        }

    }

    private void readRooms(){
        System.out.println("I am now in ReadRooms");


        // Synchronize the port number setting
        int h;
        synchronized (obj) {
            h = hash(obj.getRoom().getRoomName());
            System.out.println("hash = " + h);
            if (h == 0) {
                portNum = 4322;
            } else if (h == 1) {
                portNum = 4323;
            } else {
                portNum = 4324;
            }
        }
        System.out.println("portNum = " + portNum);
        ObjectInputStream in2 = null;
        ObjectOutputStream out2 = null;
        Socket requestSocket = null;
        try {
            requestSocket = new Socket(Master.ips.get(h), portNum);
            out2 = new ObjectOutputStream(requestSocket.getOutputStream());
            in2 = new ObjectInputStream(requestSocket.getInputStream());
            System.out.println("Sending room from MasterThread " + obj.getRoom().getRoomName() + " to the worker " + portNum);
            out2.writeObject(obj);
            out2.flush();

            ConfirmationMessage confirmation = (ConfirmationMessage) in2.readObject();
            System.out.println("Server response: " + confirmation.getMessage());

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
    }

    private int hash(String name){
        System.out.println("In hash method, Master.WorkerNum.get() = " + Master.WorkerNum.get());
        return Math.abs(name.hashCode()) % Master.WorkerNum.get();
    }

}