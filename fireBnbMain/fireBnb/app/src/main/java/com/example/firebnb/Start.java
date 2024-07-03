package com.example.firebnb;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Start {
    public static Scanner scanner = new Scanner(System.in);
    public static int roleChoiceStart;
    public static String jsonPath;
    public static String roomName;
    public static List<String> datesAvailable = new ArrayList<>();
    public static String date;
    public static String ownerName;
    public static String userName;
    public Map<String, String> roomImageMap = new HashMap<>();

    public static TCPObject obj;
    public static Room r;
    int NumOfPersons = -1;
    String Area = "_";
    int NumOfStars = -1;
    String NumOfReviews;
    String dateFilter;
    public static ArrayList<String> datesFilter = new ArrayList<>();
    String roomNameToBook;
    String dateFilterToBook;
    public static List<String> datesInspectBooking = new ArrayList<>();
    public static List<String> datesInspectBookingForFilter = new ArrayList<>();
    String dateInspectBooking;
    public static List<String> datesFilterToBook = new ArrayList<>();
    public static Booking RoomBooking = new Booking();
    String roomNameToGrade;
    int NumOfStarsToGrade;
    Boolean TASKID7 = false;
    Map<List<String>, List<Room>>  retrievedList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) throws JSONException, IOException {
        System.out.println("Welcome to the Hotel Management System!");
        System.out.println("Please choose your role:");
        System.out.println("1. Manager");
        System.out.println("2. User");
        System.out.print("Enter your choice (1 or 2): ");

        Start start = new Start();
        do {
            roleChoiceStart = scanner.nextInt();
            // Consume the newline character
            scanner.nextLine();
            if (roleChoiceStart == 1) {
                System.out.println("Please give your name:");
                ownerName = scanner.nextLine();
                System.out.println("Welcome " + ownerName + "...");
                start.startManagerApp();
            } else if (roleChoiceStart == 2) {
                System.out.println("Please give your name:");
                userName = scanner.nextLine();
                System.out.println("Welcome " + userName + "...");
                start.startUserApp();
            } else {
                System.out.println("Invalid choice. Please try again.");
                System.out.print("Enter your choice (1 or 2): ");
            }
        }while ((roleChoiceStart != 1 ) ||  (roleChoiceStart !=2));

        scanner.close();
    }

    private void startUserApp() {

        System.out.println("Choose an option:");
        System.out.println("1. Filter rooms");
        System.out.println("2. Book room");
        System.out.println("3. Grade a room");
        System.out.print("Enter your choice (1, 2 or 3): ");
        int roleChoice = scanner.nextInt();
        // Consume the newline character
        scanner.nextLine();

        if (roleChoice == 1) {
            System.out.println("FILTERS:");

            boolean numOfPersonsSet = false;
            boolean numOfReviews = false;
            boolean areaSet = false;
            boolean numOfStarsSet = false;
            boolean datesAvailableSet = false;
            ArrayList<String> mapId = new ArrayList<>();
            ArrayList<String> filter = new ArrayList<>();

            int roleChoice2;
            do {
                System.out.println("1. Number of persons");
                System.out.println("2. Area");
                System.out.println("3. Stars");
                System.out.println("4. Dates available");
                System.out.println("5. Reviews available");
                System.out.println("0. Apply filters");
                System.out.println("Please type the number of the filter to give a value or 0 to exit:");
                roleChoice2 = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                if (roleChoice2 == 1) {
                    if (numOfPersonsSet) {
                        System.out.println("Previous filter value for Number of persons was deleted.");
                    }
                    System.out.println("Give Number of persons");
                    NumOfPersons = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    numOfPersonsSet = true;
                    //filtering(String.valueOf(NumOfPersons),"noOfPersons");
                    mapId.add(String.valueOf(NumOfPersons));
                    filter.add("noOfPersons");

                } else if (roleChoice2 == 2) {
                    if (areaSet) {
                        System.out.println("Previous filter value for Area was deleted.");
                    }
                    System.out.println("Give Area");
                    Area = scanner.nextLine();
                    areaSet = true;
                    mapId.add(Area);
                    filter.add("area");

                } else if (roleChoice2 == 3) {
                    if (numOfStarsSet) {
                        System.out.println("Previous filter value for Stars was deleted.");
                    }
                    System.out.println("Give number of stars");
                    NumOfStars = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    numOfStarsSet = true;
                    mapId.add(String.valueOf(NumOfStars));
                    filter.add("stars");

                } else if (roleChoice2 == 4) {
                    if (datesAvailableSet) {
                        System.out.println("Previous filter value for Dates available was deleted.");
                    }
                    System.out.println("Give dates available");
                    System.out.println("(Type a date (format: dd-mm-yyyy) and press ENTER to confirm it. Press ENTER two times to stop.");
                    String dateFilter;
                    datesFilter.clear(); // Clear the previous dates
                    while (!(dateFilter = scanner.nextLine()).isEmpty()) {
                        // Add the date to the list
                        datesFilter.add(dateFilter);
                        //filtering(String.valueOf(dateFilter),"datesAvailable");
                        //mapId.add(dateFilter);
                        //filter.add("datesAvailable");
                    }
                    datesAvailableSet = true;
                    for (int i = 0; i < datesFilter.size(); i++) {
                        mapId.add(String.valueOf(datesFilter.get(i)));
                        filter.add("datesAvailable");
                    }

                    //filtering(String.valueOf(NumOfPersons),"noOfPersons");
                }else if (roleChoice2 == 5) {
                    if (numOfReviews) {
                        System.out.println("Previous filter value for Stars was deleted.");
                    }
                    System.out.println("Give number of reviews");
                    NumOfReviews = scanner.nextLine();
                    scanner.nextLine(); // Consume newline left-over
                    numOfReviews = true;
                    //filtering(String.valueOf(NumOfReviews),"noOfReviews");
                    mapId.add(String.valueOf(NumOfReviews));
                    filter.add("noOfReviews");
                }
            } while (roleChoice2 != 0);
            System.out.println(mapId.toString()+"      and    "+filter.toString()); ////////////////////////////////////
            if(datesFilter.isEmpty()) {
                filtering(mapId, filter);
            }else {
                filtering(mapId, filter);
            }
        } else if (roleChoice == 2) {

            System.out.println("Please type the name of the room you want to book:");
            roomNameToBook = scanner.nextLine();
            System.out.println("Please type the dates you want to book it for:");
            System.out.println("(Type a date (format: dd-mm-yyyy) and press ENTER to confirm it. Press ENTER two times to stop.");
            while (!(dateFilterToBook = scanner.nextLine()).isEmpty()) {
                // Add the date to the list
                datesFilterToBook.add(dateFilterToBook);
            }

            BookDatesClient();

        } else if (roleChoice == 3) {
            System.out.println("Please type the name of the room you want to grade:");
            roomNameToGrade = scanner.nextLine();
            System.out.println("Please type the number of stars you want to grade it with:");
            NumOfStarsToGrade = scanner.nextInt();

            GradeRoomClient();

        } else {
            System.out.println("Invalid choice. Please try again.");
        }

    }

    public void filtering(ArrayList<String> mapId,ArrayList<String> filter) {

        System.out.println("mapid: "+mapId.toString()+"  filter: "+filter.toString());

        TCPObject obj = new TCPObject(mapId, filter, 3);
        new ClientUser(obj).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startManagerApp() throws JSONException, IOException {
        System.out.println("Choose an option:");
        System.out.println("1. Add rooms");
        System.out.println("2. Add rooms' available dates");
        System.out.println("3. Inspect rooms");
        System.out.println("4. Inspect bookings");
        System.out.print("Enter your choice (1, 2 or 3): ");
        int roleChoice = scanner.nextInt();
        // Consume the newline character
        scanner.nextLine();

        if (roleChoice == 1) {
            System.out.println("Please give the path of the .json file that contains the rooms you want to add:");
            jsonPath = scanner.nextLine();
            AddRoomsClient();
        } else if (roleChoice == 2) {
            System.out.println("Please type the name of the room:");
            roomName = scanner.nextLine();
            System.out.println("Please type available dates for " + roomName + ":");
            System.out.println("(Type a date (format: dd-mm-yyyy) and press ENTER to confirm it. Press ENTER two times to stop.");
            date = scanner.nextLine();
            // Check if the user pressed Enter without typing a date
            while (!date.isEmpty()) {
                // Add the date to the list
                datesAvailable.add(date);
                // Read the next date
                date = scanner.nextLine();
            }

            AddDatesClient();

            System.out.println("Dates added successfully");
            /*
            for (String date : datesAvailable){
                System.out.println(date);
            }*/

        } else if (roleChoice == 3) {
            System.out.println("-------------------------------------------");
            System.out.println("YOUR ROOMS:");
            System.out.println("-------------------------------------------");
            printBookingsClient(3);

        } else if (roleChoice == 4) {
            ArrayList<String> mapId = new ArrayList<>();
            ArrayList<String> filter = new ArrayList<>();

            System.out.println("Please type the date period you want(eg 12-5-24, enter, 12-6-24, enter, enter):");
            System.out.println("(Type a date (format: dd-mm-yyyy) and press ENTER to confirm it. Press ENTER two times to stop.");
            dateInspectBooking = scanner.nextLine();
            // Check if the user pressed Enter without typing a date
            while (!dateInspectBooking.isEmpty()) {
                // Add the date to the list
                datesInspectBooking.add(dateInspectBooking);
                // Read the next date
                dateInspectBooking = scanner.nextLine();
            }

            datesInspectBookingForFilter = extractDateRange(datesInspectBooking.get(0), datesInspectBooking.get(1));

            TASKID7 = true;

            printBookingsClient(7);

        } else {
            System.out.println("Invalid choice. Please try again.");
        }

    }

    private void printBookingsClient(int taskID) {

        ArrayList<String> mapId = new ArrayList<>();
        ArrayList<String> filter = new ArrayList<>();


        mapId.add(ownerName);
        filter.add("owner");

        TCPObject obj = new TCPObject(mapId, filter, taskID);
        new ClientPrintBookings(obj).start();

    }

    private void AddDatesClient() {

        r = new Room(roomName, 0, null, null, 0, null, datesAvailable, null, ownerName, null);
        obj = new TCPObject(r, 2);

        new ClientAddDates(obj).start();

    }

    private void BookDatesClient() {

        RoomBooking.setUsername(userName);
        RoomBooking.setDates(datesFilterToBook);
        RoomBooking.printBooking();

        obj = new TCPObject(roomNameToBook, RoomBooking, 5);

        new ClientAddBooking(obj).start();

    }


    private void GradeRoomClient() {

        obj = new TCPObject(roomNameToGrade, NumOfStarsToGrade, 6);
        new ClientAddGrade(obj).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void AddRoomsClient() throws IOException {
        String jsonFilePath = jsonPath;
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)), StandardCharsets.UTF_8);

        // Define the type of the object we expect to parse
        Type roomListType = new TypeToken<List<Room>>() {}.getType();

        // Create a Gson instance and parse the JSON string to a list of Room objects
        Gson gson = new Gson();
        List<Room> rooms = gson.fromJson(jsonString, roomListType);

        for (Room room : rooms) {
            new Client(room).start();
        }
    }







    public class Client extends Thread {
        private Room room;

        public Client(Room room) {
            this.room = room;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                TCPObject TCPObject = new TCPObject(room, 1);

                System.out.println("Sending room " + room.getRoomName() + " to the master...");
                out.writeObject(TCPObject);
                out.flush();

                ConfirmationMessage confirmation = (ConfirmationMessage) in.readObject();
                System.out.println("Server response: " + confirmation.getMessage());

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

    }

    public class ClientAddGrade extends Thread {

        private TCPObject obj;

        public ClientAddGrade(TCPObject obj) {
            this.obj = obj;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                //System.out.println("Sending object to the master..." + obj.getRoom().print());
                out.writeObject(obj); // Sending the Object instance
                out.flush();

                ConfirmationMessage confirmation = (ConfirmationMessage) in.readObject();
                System.out.println("Server response: " + confirmation.getMessage());

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
    }

    public class ClientAddDates extends Thread {

        private TCPObject obj;

        public ClientAddDates(TCPObject obj) {
            this.obj = obj;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                //System.out.println("Sending object to the master..." + obj.getRoom().print());
                out.writeObject(obj); // Sending the Object instance
                out.flush();

                ConfirmationMessage confirmation = (ConfirmationMessage) in.readObject();
                System.out.println("Server response: " + confirmation.getMessage());

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
    }

    public class ClientAddBooking extends Thread {

        private TCPObject obj;

        public ClientAddBooking(TCPObject obj) {
            this.obj = obj;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                //System.out.println("Sending object to the master..." + obj.getRoom().print());
                out.writeObject(obj); // Sending the Object instance
                out.flush();

                ConfirmationMessage confirmation = (ConfirmationMessage) in.readObject();
                System.out.println("Server response: " + confirmation.getMessage());

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
    }


    public class ClientPrintBookings extends Thread {

        private TCPObject obj;

        public ClientPrintBookings(TCPObject obj) {

            this.obj = obj;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                out.writeObject(obj); // Sending the Object instance
                out.flush();

                if (obj.getTaskId() == 7){
                    out.writeObject(datesInspectBookingForFilter); // Sending the Object instance
                    out.flush();
                }

                int ID=0;

                try {
                    ID = (int) in.readInt();
                    // Process data
                } catch (EOFException e) {
                    System.err.println("Reached end of file unexpectedly: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                    e.printStackTrace();
                }

                if(ID == 7){
                    System.out.println("Output...");

                    List<List<String>> bookingCounts = (List<List<String>>) in.readObject();
                    for (List<String> info : bookingCounts) {
                        System.out.println(info.get(0) + ": " + info.get(1));
                    }
                } else if (ID == 3) { //retern from filters

                    roomImageMap = (Map<String, String>) in.readObject();

                    retrievedList = (Map<List<String>, List<Room>>) in.readObject();
                    System.out.println("MAP = " + retrievedList.toString() + "\n");

                    for (List<Room> ListRoom : retrievedList.values()){
                        for (Room room : ListRoom){
                            System.out.println(room.print());
                        }
                    }

                }
                else if (ID == 9) {
                    System.out.println("An error occurred, please try again!");
                }

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
        /*

        // Static factory method to create and start a new ClientAddDates instance
        public static void main(String[] args) {

            TCPObject = new TCPObject("Billy","owner",3);
            new ClientPrintBookings(TCPObject).start();
        }*/
    }



    public class ClientUser extends Thread {

        private TCPObject TCPObject;

        public ClientUser(TCPObject TCPObject) {

            this.TCPObject = TCPObject;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                System.out.println("Sending object to the master...");
                out.writeObject(TCPObject); // Sending the Object instance
                out.flush();

                int ID = in.readInt();

                if (ID == 3) {
                    retrievedList = (Map<List<String>, List<Room>>) in.readObject();
                    System.out.println("MAP = " + retrievedList.toString() + "\n");

                    for (List<Room> ListRoom : retrievedList.values()) {
                        for (Room room : ListRoom) {
                            System.out.println(room.print());
                        }
                    }
                }

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

    }

    public static List<String> extractDateRange(String startDate, String endDate) {
        List<String> dateRange = new ArrayList<>();

        // Split the start and end dates into components
        String[] startComponents = startDate.split("-");
        String[] endComponents = endDate.split("-");

        int startDay = Integer.parseInt(startComponents[0]);
        int startMonth = Integer.parseInt(startComponents[1]);
        int startYear = Integer.parseInt(startComponents[2].substring(0, 2));

        int endDay = Integer.parseInt(endComponents[0]);
        int endMonth = Integer.parseInt(endComponents[1]);
        int endYear = Integer.parseInt(endComponents[2].substring(0, 2));

        // Ensure the start date is before the end date
        if (startYear > endYear || (startYear == endYear && startMonth > endMonth) || (startYear == endYear && startMonth == endMonth && startDay > endDay)) {
            throw new IllegalArgumentException("Start date must be before the end date.");
        }

        // Calculate the number of days between the start and end dates
        int daysBetween = (endYear - startYear) * 365 + (endMonth - startMonth) * 30 + (endDay - startDay);

        // Generate and add each date in the range to the list
        for (int i = 0; i <= daysBetween; i++) {
            int day = startDay + i;
            int month = startMonth;
            int year = startYear;

            // Adjust day and month if they exceed maximum values
            while (day > 31) {
                day -= 31;
                month++;
            }
            while (month > 12) {
                month -= 12;
                year++;
            }

            // Format the date as a string and add it to the list
            dateRange.add(String.format("%02d-%02d-%02d", day, month, year % 100));
        }

        return dateRange;
    }



}
