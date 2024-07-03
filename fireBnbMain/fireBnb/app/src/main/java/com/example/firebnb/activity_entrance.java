package com.example.firebnb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;


public class activity_entrance extends AppCompatActivity {

    private ImageButton next;
    private SharedPreferences sh;
    private TextInputLayout name;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        next = (ImageButton) findViewById(R.id.next);

        name = findViewById(R.id.Name);

        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                username = name.getEditText().getText().toString().trim();

                Intent intent = new Intent(activity_entrance.this, activity_filters.class);
                intent.putExtra("name", username);
                startActivity(intent);
            }
        });
    }


}