package com.example.firebnb;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class activity_book extends AppCompatActivity {

    private Button pickDateBtn,pickDateBtnTo,book;
    private TextView personsTextView,starsTextView,reviewsTestView,name,price,pricetag;
    private String from,to,username;
    private List<String> datelist;
    private Handler mainHandler;
    private ImageView star1, star2, star3, star4, star5, imageView3;

    private Room room;

    public static Booking RoomBooking = new Booking();

    String roomNameToGrade;
    int NumOfStarsToGrade;
    public static TCPObject obj;
    public String x;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                Booking book = (Booking) bundle.getSerializable("roomBook");

                Intent intent = new Intent(activity_book.this, activity_complete.class);
                intent.putExtra("roomBook", (Serializable) book);
                startActivity(intent);
                return true;
            }
        });

        room = (Room) getIntent().getSerializableExtra("Room");
        datelist = (List<String>) getIntent().getSerializableExtra("datesToBook");
        username = (String) getIntent().getSerializableExtra("name");
        from = (String) getIntent().getSerializableExtra("from");
        to = (String) getIntent().getSerializableExtra("to");

        imageView3 = findViewById(R.id.imageView3);
        starsTextView = findViewById(R.id.textStars);
        reviewsTestView = findViewById(R.id.textReviews);
        personsTextView = findViewById(R.id.textPerson);
        name = findViewById(R.id.textView6);
        book = findViewById(R.id.button3);
        price = findViewById(R.id.price);
        pricetag = findViewById(R.id.pricetag);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        //Dates
        pickDateBtn = findViewById(R.id.back);
        pickDateBtnTo = findViewById(R.id.toD);

        String roomNameForpath = room.getRoomName();

        String roomimage = "received_image_" + roomNameForpath + ".png";

        Bitmap bitmap = loadImageFromStorage(roomimage); // Adjust the filename accordingly
        if (bitmap!= null) {
            imageView3.setImageBitmap(bitmap);
        } else {
            Log.d(" NULLLLLLL",roomimage);
        }


        //setters
        name.setText(room.getRoomName());
        starsTextView.setText(String.valueOf((int)room.getStars().getAverage())+"/5");
        reviewsTestView.setText(String.valueOf(room.getNoOfReviews()));
        personsTextView.setText(String.valueOf(room.getNoOfPersons()));
        pickDateBtn.setText(from);
        pickDateBtnTo.setText(to);
        price.setText(getPrice() + "$");
        x = String.valueOf(calculateDaysBetween(from, to));
        pricetag.setText("/ for " + x + " nights");

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDatesClient();
            }
        });

        star1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.starsgold);
                roomNameToGrade = room.getRoomName();
                NumOfStarsToGrade = 1;
                GradeRoomClient();
                Toast.makeText(activity_book.this, "Room graded with " + NumOfStarsToGrade + " stars!", Toast.LENGTH_SHORT).show();
            }

        });

        star2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.starsgold);
                star2.setImageResource(R.drawable.starsgold);
                roomNameToGrade = room.getRoomName();
                NumOfStarsToGrade = 2;
                GradeRoomClient();
                Toast.makeText(activity_book.this, "Room graded with " + NumOfStarsToGrade + " stars!", Toast.LENGTH_SHORT).show();
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.starsgold);
                star2.setImageResource(R.drawable.starsgold);
                star3.setImageResource(R.drawable.starsgold);
                roomNameToGrade = room.getRoomName();
                NumOfStarsToGrade = 3;
                GradeRoomClient();
                Toast.makeText(activity_book.this, "Room graded with " + NumOfStarsToGrade + " stars!", Toast.LENGTH_SHORT).show();
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.starsgold);
                star2.setImageResource(R.drawable.starsgold);
                star3.setImageResource(R.drawable.starsgold);
                star4.setImageResource(R.drawable.starsgold);
                roomNameToGrade = room.getRoomName();
                NumOfStarsToGrade = 4;
                GradeRoomClient();
                Toast.makeText(activity_book.this, "Room graded with " + NumOfStarsToGrade + " stars!", Toast.LENGTH_SHORT).show();
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.starsgold);
                star2.setImageResource(R.drawable.starsgold);
                star3.setImageResource(R.drawable.starsgold);
                star4.setImageResource(R.drawable.starsgold);
                star5.setImageResource(R.drawable.starsgold);
                roomNameToGrade = room.getRoomName();
                NumOfStarsToGrade = 5;
                GradeRoomClient();
                Toast.makeText(activity_book.this, "Room graded with " + NumOfStarsToGrade + " stars!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getPrice(){
        Random rand = new Random(); // Create a new instance of Random class
        int minStep = 0; // Minimum step index (0 to 19)
        int maxStep = 20; // Maximum step index (0 to 19)
        int stepSize = 25; // Step size
        // Generate a random number between minStep and maxStep
        int randomStepIndex = rand.nextInt(maxStep - minStep + 1) + minStep;
        // Calculate the starting point of the price range based on the random step index
        int startPoint = randomStepIndex * stepSize;
        // Add the starting point to the minimum price to get the final price
        return String.valueOf(startPoint + 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int calculateDaysBetween(String dateA, String dateB) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate localDateA = LocalDate.parse(dateA, formatter);
        LocalDate localDateB = LocalDate.parse(dateB, formatter);

        long daysBetween = ChronoUnit.DAYS.between(localDateA, localDateB);
        return Math.toIntExact(daysBetween);
    }

    private void BookDatesClient() {

        RoomBooking.setUsername(username);
        RoomBooking.setDates(datelist);
        //RoomBooking.printBooking();

        Log.d("LLLLLLIIIIISSSSSTTTTT",datelist.toString());

        TCPObject obj = new TCPObject(room.getRoomName(), RoomBooking, 5);

        new ClientAddBooking(obj,mainHandler).start();

    }

    public class ClientAddBooking extends Thread {

        private TCPObject obj;
        private Handler handler;

        public ClientAddBooking(TCPObject obj,Handler handler) {
            this.obj = obj;
            this.handler = handler;
        }

        public void run() {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                requestSocket = new Socket("192.168.1.5", 4321);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                //System.out.println("Sending object to the master..." + obj.getRoom().print());
                out.writeObject(obj); // Sending the Object instance
                out.flush();

                ConfirmationMessage confirmation = (ConfirmationMessage) in.readObject();
                System.out.println("Server response: " + confirmation.getMessage());

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putSerializable("roomBook", RoomBooking);
                message.setData(bundle);
                handler.sendMessage(message);

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

    private void GradeRoomClient() {

        obj = new TCPObject(roomNameToGrade, NumOfStarsToGrade, 6);
        //System.out.println("roomNameToGrade = " + roomNameToGrade);
        new ClientAddGrade(obj).start();

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
                requestSocket = new Socket("192.168.1.5", 4321);
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

    private Bitmap loadImageFromStorage(String fileName) {
        String filePath = getApplicationContext().getFilesDir() + "/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            Log.w("gata1", "File does not exist: " + filePath);
            return null; // Or handle the case appropriately
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            Log.e("gata2", "File not found: " + e.getMessage());
            return null; // Or handle the case appropriately
        } catch (IOException e) {
            Log.e("gata3", "IO error reading file: " + e.getMessage());
            return null; // Or handle the case appropriately
        }
    }
}
