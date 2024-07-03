package com.example.firebnb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reducer {

    public static void main(String[] args) {
        new Reducer().openServer();
    }

    ServerSocket providerSocket;
    Socket connection = null;
    public static Map<List<String>, List<Room>> finalmap = new HashMap<List<String>, List<Room>>();
    public static Integer mapnum = 0;

    void openServer() {
        try {
            providerSocket = new ServerSocket(4325, 10);

            while (true) {
                System.out.println("Reducer started and LISTENING...");
                connection = providerSocket.accept();

                Thread t = new ReducerThread(connection);
                t.start();

            }

        } catch (SocketException SE){
            System.out.println("Reducer SE EXCEPTION IN SERVER...");
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

}
