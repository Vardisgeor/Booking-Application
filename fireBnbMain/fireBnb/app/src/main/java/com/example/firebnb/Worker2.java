package com.example.firebnb;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Worker2 {

    // Constructor that accepts a port number
    public Worker2(int port) {
        try {
            providerSocket = new ServerSocket(port, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ServerSocket providerSocket;
    Socket connection = null;
    public static List<Room> rooms = new ArrayList<>();

    void openServer() {
        try {
            System.out.println("Worker is running and listening on port " + providerSocket.getLocalPort() + "...");

            while (true) {
                System.out.println("LISTENING...");
                connection = providerSocket.accept();

                Thread t = new WorkerThread2(connection, this); // Pass 'this' to give WorkerThread access to the Worker instance
                t.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (providerSocket != null) providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        System.out.println("NOT LISTENING...");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            TCPObject obj = new TCPObject(0);

            out.writeObject(obj);
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

        String filePath = "/Users/vardisgeorgilas/Desktop/fireBnbMain/fireBnb/app/src/main/java/com/example/firebnb/room4323.json";

        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            Worker2.rooms = readRoomsFromJson(filePath);
            System.out.println("JUST READ ROOMS");

            for (Room room : Worker2.rooms){
                System.out.println(room.print());
            }
        } else {
            System.out.println("File does not exist: " + filePath);
            // Handle the case where the file does not exist
        }

        new Worker2(4323).openServer();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized static List<Room> readRoomsFromJson(String filePath) {
        List<Room> rooms = new ArrayList<>();
        try {

            String content = null;
            //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            //}

            Gson gson = new Gson();
            Type type = new TypeToken<List<Room>>(){}.getType();
            rooms = gson.fromJson(content, type);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Size = " + rooms.size());

        return rooms;
    }

    public static synchronized void saveRoomsToJson(int i) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Room>>() {}.getType();

        try (FileWriter writer = new FileWriter("/Users/vardisgeorgilas/Desktop/fireBnbMain/fireBnb/app/src/main/java/com/example/firebnb/room"+i+".json")) {
            gson.toJson(rooms, type, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
