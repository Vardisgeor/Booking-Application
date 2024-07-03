package com.example.firebnb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

public class activity_login extends AppCompatActivity {

    private ImageButton goback,enter;
    private TextInputLayout input_username,input_password;
    private String username,password;
    private Account acc;
    private SharedPreferences sh;
    private  SharedPreferences.Editor editor;
    private Set<Account> accountSet;
    private String json;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sh = getSharedPreferences("data",MODE_PRIVATE);
        editor = sh.edit();
        if(sh.contains("data")){
            Gson gson = new Gson();
            json = sh.getString("data", "");
            Type setType = new TypeToken<Set<Account>>() {}.getType();
            accountSet = gson.fromJson(json, setType);
            Account.updateAcc(accountSet);
        }

        goback = (ImageButton) findViewById(R.id.prevButton);
        enter = (ImageButton) findViewById(R.id.loginexe);

        input_username = findViewById(R.id.Name);
        input_password = findViewById(R.id.Password);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sh = getSharedPreferences("data",MODE_PRIVATE);
                editor = sh.edit();
                if(sh.contains("data")){
                    Gson gson = new Gson();
                    String json = sh.getString("data", "");
                    Type setType = new TypeToken<Set<Account>>() {}.getType();
                    accountSet = gson.fromJson(json, setType);
                    Account.updateAcc(accountSet);
                }
                username = input_username.getEditText().getText().toString().trim();
                password = input_password.getEditText().getText().toString().trim();

                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(activity_login.this, "Please fill the fields!", Toast.LENGTH_SHORT).show();

                }else{
                    acc = Account.Login(username,password);
                    if(acc!=null){
                        next();
                    }else{
                        Toast.makeText(activity_login.this, "Wrong input data, please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void back(){
        Intent intent = new Intent(this, activity_entrance.class);
        startActivity(intent);
    }

    private void next(){

        Intent intent = new Intent(this,activity_entrance.class);
        intent.putExtra("Account", acc.getName());
        startActivity(intent);

    }

}
