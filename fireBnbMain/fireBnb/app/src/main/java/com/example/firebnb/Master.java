package com.example.firebnb;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

public class Master {

	public static void main(String[] args) {
		new Master().openServer();
	}

	ServerSocket providerSocket;
	Socket connection = null;
	public static AtomicInteger WorkerNum = new AtomicInteger(0);
	public static List<ObjectOutputStream> connectionsList = new ArrayList<>();
	public static AtomicInteger var = new AtomicInteger(0);
	public static boolean TASKID7 = false;
	public static List<String> datesInspectBookingForFilter = new ArrayList<>();
	public static boolean InPrintBooking = false;
	private int i =0;

	public static List<String> ips = new ArrayList<>();

	void openServer() {
		try {
			providerSocket = new ServerSocket(4321, 10);

			while (true) {
				System.out.println("Master LISTENING....");
				connection = providerSocket.accept();
				i = i + 1;
				// Handle the connection in a new thread
				if(ips.size()<3) {
					ips.add(connection.getInetAddress().getHostAddress());

				}
				if(i==5){
					ips.set(0,"127.0.0.1");
				}

				System.out.println(ips);
				Thread t = new MasterThread(connection);
				//System.out.println("Master WorkerNum = " + WorkerNum);
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


}
