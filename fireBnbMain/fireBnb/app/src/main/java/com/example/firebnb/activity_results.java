package com.example.firebnb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class activity_results extends AppCompatActivity {

    public static List<Room> rooms = new ArrayList<>();

    private  ListView listView;
    private ImageView image;
    private Button details;
    private String from,to,username;
    private List<String> datelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        from = (String) getIntent().getSerializableExtra("from");
        to = (String) getIntent().getSerializableExtra("to");
        List<Room> results = (List<Room>) getIntent().getSerializableExtra("results");
        datelist = (List<String>) getIntent().getSerializableExtra("datesToBook");
        username = (String) getIntent().getSerializableExtra("name");


        Log.d("MIAOUUU",results.toString());



        listView = (ListView) findViewById(R.id.listview1);
        ArrayAdapter<Room> arrayAdapter = new ArrayAdapter<Room>(this, R.layout.listview, R.id.textView10, results) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                image = view.findViewById(R.id.imageView);
                details = view.findViewById(R.id.details);

                TextView roomName = view.findViewById(R.id.textView10);

                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity_results.this,activity_book.class);
                        i.putExtra("Room",results.get(position));
                        i.putExtra("from",from);
                        i.putExtra("to",to);
                        i.putExtra("datesToBook",(Serializable) datelist);
                        i.putExtra("name",username);
                        startActivity(i);
                    }
                });


                roomName.setText(results.get(position).getRoomName());//name



                String roomNameForpath = results.get(position).getRoomName();

                String roomimage = "received_image_" + roomNameForpath + ".png";


                Log.d("roomimage =================== ",roomimage);


                Bitmap bitmap = loadImageFromStorage(roomimage); // Adjust the filename accordingly
                if (bitmap!= null) {
                    image.setImageBitmap(bitmap);
                } else {
                    Log.d(" NULLLLLLL",roomimage);
                }



                return view;
            }
        };

        listView.setAdapter(arrayAdapter);



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

