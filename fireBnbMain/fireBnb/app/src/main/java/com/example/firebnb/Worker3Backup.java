package com.example.firebnb;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Worker3Backup {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private List<Room> Backuprooms;

    public static void main(String[] args) {
        new Worker3Backup().openServer();
    }

    private Future<?> futureTask;
    Socket connection = null;

    public static List<Room> rooms = new ArrayList<>();


    // Existing fields and methods...

    void openServer() {
        ServerSocket providerSocket = null;
        try {
            providerSocket = new ServerSocket(4327, 10);
            System.out.println("Server started.");

            // Wrap the task in a Callable and submit it to the scheduler
            futureTask = scheduler.scheduleAtFixedRate(() -> {
                System.out.println("MPHKAAA.");
                if (!isOtherServerUp("127.0.0.1", 4322)) { // Replace with actual IP and port
                    System.out.println("Worker3 is down.");

                    NotifyMaster();
                    //NotifyReducer();

                    openServerWorker3Backup();

                    stopScheduler();

                } else {
                    System.out.println("Worker3 is not down.");
                }
            }, 0, 1, TimeUnit.SECONDS); // Check every second

            // Existing server loop...
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (providerSocket!= null) providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    private void openServerWorker3Backup() {

        ServerSocket providerSocket = null;
        Socket connection = null;

        try {
            providerSocket = new ServerSocket(4322, 10);

            while (true) {
                System.out.println("LISTENING...");
                connection = providerSocket.accept();

                Thread t = new Worker3BackupThread(connection, this); // Pass 'this' to give WorkerThread access to the Worker instance
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



    }

    private void NotifyReducer() {

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("192.168.89.156", 4325);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            System.out.println("Notify!!!!!!!!!!Reducer");

            out.writeInt(10);
            out.flush();

            ConfirmationMessage confirmation = new ConfirmationMessage("Request received and processed.");
            out.writeObject(confirmation);
            out.flush();


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (SocketException SE){
            System.out.println("SocketException!!!!!!!!!!Reducer");
        } catch (IOException ignored) {
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

    private void NotifyMaster() {

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            TCPObject obj = new TCPObject(9);

            out.writeObject(obj);
            out.flush();

            ConfirmationMessage confirmation = new ConfirmationMessage("Request received and processed.");
            out.writeObject(confirmation);
            out.flush();


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (SocketException SE){
            System.out.println("SocketException!!!!!!!!!!Master");
        } catch (IOException ignored) {
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (requestSocket != null) requestSocket.close();
            } catch (SocketException SE) {
                System.out.println("SocketException22222!!!!!!!!!!Master");
                NotifyReducer();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private boolean isOtherServerUp(String ipAddress, int port) {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket(ipAddress, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            TCPObject obj = new TCPObject(8);

            out.writeObject(obj);
            out.flush();

            Backuprooms = (List<Room>) in.readObject();

            System.out.println(Backuprooms.toString());

            rooms = Backuprooms;


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            return false;
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
        return true;
    }

    public void stopScheduler() {
        if (futureTask!= null &&!futureTask.isCancelled()) {
            futureTask.cancel(true); // Attempt to cancel the task
            scheduler.shutdown(); // Shut down the scheduler
        }
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
        return rooms;
    }

    public static synchronized void saveRoomsToJson(int i) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Room>>() {}.getType();

        try (FileWriter writer = new FileWriter("/Users/vardisgeorgilas/Desktop/fireBnbMain/fireBnb/app/src/main/java/com/example/firebnb/room4322"+i+".json")) {
            gson.toJson(rooms, type, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    // Other existing methods...
}

