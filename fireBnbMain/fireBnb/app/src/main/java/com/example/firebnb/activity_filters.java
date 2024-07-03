package com.example.firebnb;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.O)
public class activity_filters extends AppCompatActivity {

    private int numberOfPersons = 0;

    private Button pickDateBtn,pickDateBtnTo;
    private int persons,reviews,stars;
    private String area,from,to,username;
    private TextView personsTextView,starsTextView,reviewsTestView;
    private Button next,minusButton, plusButton,minusButtonStars, plusButtonStars,minusButtonReviews, plusButtonReviews;
    private EditText areaEditText;
    private ArrayList<String> mapId, filter;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
    private LocalDate toDate,fromDate;
    private Handler mainHandler;
    private List<String> dateList;
    Map<List<String>, List<Room>>  retrievedList;
    public Map<String, String> roomImageMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        username = (String) getIntent().getSerializableExtra("name");

        mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                List<Room> results = (List<Room>) bundle.getSerializable("results");

                // Start results_activity with the results
                Intent intent = new Intent(activity_filters.this, activity_results.class);
                intent.putExtra("results", (Serializable) results);
                intent.putExtra("from",from);
                intent.putExtra("to",to);
                intent.putExtra("datesToBook",(Serializable) dateList);
                intent.putExtra("name",username);

                startActivity(intent);
                return true;
            }
        });


        //texts input
        areaEditText = findViewById(R.id.txt);

        //Number of stars
        starsTextView = findViewById(R.id.Stars);
        minusButtonStars = findViewById(R.id.minusButtonStars);
        plusButtonStars = findViewById(R.id.plusButtonStars);

        //Number of reviews
        reviewsTestView = findViewById(R.id.Reviews);
        minusButtonReviews = findViewById(R.id.minusButtonReviews);
        plusButtonReviews = findViewById(R.id.plusButtonReviews);

        //Number of persons
        personsTextView = findViewById(R.id.Persons);
        minusButton = findViewById(R.id.minusButton);
        plusButton = findViewById(R.id.plusButton);

        //Dates
        pickDateBtn = findViewById(R.id.back);
        pickDateBtnTo = findViewById(R.id.toD);

        // continue
        next = (Button) findViewById(R.id.loginexe);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapId = new ArrayList<>();
                filter = new ArrayList<>();


                area = areaEditText.getText().toString().trim();
                Log.d("activity_filters",area);
                if (area.isEmpty() || from.equals(null) || to.equals(null)) {
                    Toast.makeText(activity_filters.this, "Please fill all the details.", Toast.LENGTH_SHORT).show();

                }else{
                    //set filters
                    mapId.add(area);
                    filter.add("area");

                    if(stars>0){
                        mapId.add(String.valueOf(stars));
                        filter.add("stars");
                    }
                    if(persons>0){
                        mapId.add(String.valueOf(persons));
                        filter.add("noOfPersons");
                    }
                    if(reviews>0){
                        mapId.add(String.valueOf(reviews));
                        filter.add("noOfReviews");
                    }

                    dateList = getDatesBetween(fromDate, toDate, formatter);
                    for (String date : dateList) {
                        mapId.add(date);
                        filter.add("datesAvailable");// You can replace this with displaying dates in your UI

                    }


                    Log.d("activity_filters","mapid: "+mapId.toString()+"  filter: "+filter.toString());

                    filtering(mapId,filter);

                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementPersons();
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementPersons();
            }
        });

        minusButtonStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementStars();
            }
        });

        plusButtonStars.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementStars();
            }
        });

        minusButtonReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementReviews();
            }
        });

        plusButtonReviews.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementReviews();
            }
        });

        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        activity_filters.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.


                                from = String.format("%02d-%02d-%02d", dayOfMonth, monthOfYear + 1, year % 100);
                                pickDateBtn.setText(from);
                                from = pickDateBtn.getText().toString();
                                fromDate = LocalDate.parse(from, formatter);
                            }
                        },

                        year, month, day);

                datePickerDialog.show();
            }
        });

        pickDateBtnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        activity_filters.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                to = String.format("%02d-%02d-%02d", dayOfMonth, monthOfYear + 1, year % 100);
                                pickDateBtnTo.setText(to);
                                to = pickDateBtnTo.getText().toString();
                                toDate = LocalDate.parse(to, formatter);
                            }
                        },

                        year, month, day);

                datePickerDialog.show();
            }
        });
    }

    private List<String> getDatesBetween(LocalDate startDate, LocalDate endDate, DateTimeFormatter formatter) {
        List<String> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }



    private void incrementPersons() {
        int currentPersons = Integer.parseInt(personsTextView.getText().toString());
        currentPersons++;
        personsTextView.setText(String.valueOf(currentPersons));
        personsTextView.setTextColor(Color.WHITE);
        persons = currentPersons;

    }

    private void decrementPersons() {
        int currentPersons = Integer.parseInt(personsTextView.getText().toString());
        if (currentPersons > 0) {
            currentPersons--;
            personsTextView.setText(String.valueOf(currentPersons));
            if(currentPersons==0){
                personsTextView.setTextColor(Color.GRAY);
            }
            persons = currentPersons;

        }

    }

    private void incrementStars() {
        int currentStars = Integer.parseInt(starsTextView.getText().toString());
        currentStars++;
        starsTextView.setText(String.valueOf(currentStars));
        starsTextView.setTextColor(Color.WHITE);
        stars = currentStars;


    }

    private void decrementStars() {
        int currentStars = Integer.parseInt(starsTextView.getText().toString());
        if (currentStars > 0) {
            currentStars--;
            starsTextView.setText(String.valueOf(currentStars));
            if(currentStars==0){
                starsTextView.setTextColor(Color.GRAY);
            }
            stars = currentStars;

        }

    }

    private void incrementReviews() {
        int currentRieviews = Integer.parseInt(reviewsTestView.getText().toString());
        currentRieviews++;
        reviewsTestView.setText(String.valueOf(currentRieviews));
        reviewsTestView.setTextColor(Color.WHITE);
        reviews = currentRieviews;
    }

    private void decrementReviews() {
        int currentRieviews = Integer.parseInt(reviewsTestView.getText().toString());
        if (currentRieviews > 0) {
            currentRieviews--;
            reviewsTestView.setText(String.valueOf(currentRieviews));
            if(currentRieviews==0){
                reviewsTestView.setTextColor(Color.GRAY);
            }
            reviews = currentRieviews;

        }
    }

    private void filtering(ArrayList<String> mapId,ArrayList<String> filter) {


        TCPObject obj = new TCPObject(mapId, filter, 3);
        new ClientUser(obj,mainHandler).start();
    }


    public class ClientUser extends Thread {

        private  TCPObject TCPObject;
        private  TCPObject obj2;
        private Handler handler;

        public ClientUser(TCPObject TCPObject, Handler handler) {

            this.TCPObject = TCPObject;
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

                System.out.println("Sending object to the master...");
                out.writeObject(TCPObject); // Sending the Object instance
                out.flush();

                int ID = in.readInt();

                if (ID == 3) {

                    roomImageMap = (Map<String, String>) in.readObject();

                    retrievedList = (Map<List<String>, List<Room>>) in.readObject();

                    for (Map.Entry<String, String> entry : roomImageMap.entrySet()) {
                        String roomId = entry.getKey(); // Get the room ID
                        String base64Image = entry.getValue(); // Get the Base64-encoded image string

                        byte[] imageData = Base64.getDecoder().decode(base64Image);

                        // Generate a unique filename
                        String filename = "received_image_" + roomId + ".png";
                        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
                            fos.write(imageData);
                        } catch (IOException e) {
                            System.out.println("Error writing received image: " + e.getMessage());
                        }
                    }

                    Log.d("HELLO", "OOOOOOOLA KALA BGHKA APO ID=3");

                }


                List<Room> result = new ArrayList<>();
                for (List<Room> roomList : retrievedList.values()) {
                    result.addAll(roomList);
                }

                Log.d("GAVVVVVV",result.toString());

                Log.d("HELLO2", "LIGO PRIN TON HANDLER");

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putSerializable("results", (Serializable) result);
                message.setData(bundle);
                handler.sendMessage(message);

            } catch (EOFException e) {
                // Handle EOFException
                System.err.println("End of file reached unexpectedly: MIAOU");
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


}
