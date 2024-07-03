package com.example.firebnb;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NetworkRunnable implements Runnable {
    private Handler mainHandler;
    private Map<List<String>, List<Room>> finalMap;

    public NetworkRunnable(Handler mainHandler, Map<List<String>, List<Room>> finalMap) {
        this.mainHandler = mainHandler;
        this.finalMap = finalMap;
    }

    @Override
    public void run() {
        // Perform network operation to communicate with the server
        // Simulating network operation with a placeholder result
        List<Room> results = performNetworkOperation(finalMap);

        // Pass results back to main thread using the handler
        Message message = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("results", (Serializable) results);
        message.setData(bundle);
        mainHandler.sendMessage(message);
    }

    private List<Room> performNetworkOperation(Map<List<String>, List<Room>> finalMap) {
        // Simulate network operation
        // Replace with actual server communication code
        List<Room> result = new ArrayList<>();
        for (List<Room> roomList : finalMap.values()) {
            result.addAll(roomList);
        }
        return result;
    }
}
