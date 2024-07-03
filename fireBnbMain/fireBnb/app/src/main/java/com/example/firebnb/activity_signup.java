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

public class activity_signup extends AppCompatActivity {

    private ImageButton goback,enter;
    private TextInputLayout input_username,input_password;
    private String username,password;
    private Account acc;
    private  SharedPreferences sh;
    private  SharedPreferences.Editor editor;
    private Set<Account> accountSet;


    /**
     * Μέθοδος που καλείται κατά τη δημιουργία της δραστηριότητας.
     *
     * @param savedInstanceState Τα αποθηκευμένα δεδομένα της δραστηριότητας, αν υπάρχουν.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sh = getSharedPreferences("data",MODE_PRIVATE);
        editor = sh.edit();
        if(sh.contains("data")){
            Gson gson = new Gson();
            String json = sh.getString("data", "");
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
                username = input_username.getEditText().getText().toString().trim();
                password = input_password.getEditText().getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(activity_signup.this, "Please fill the fields!", Toast.LENGTH_SHORT).show();

                } else if (Account.nameExist(username)) {
                    Toast.makeText(activity_signup.this, "Sorry, enter another username.", Toast.LENGTH_SHORT).show();

                }else{
                    acc = new Account(username,password);
                    next();
                }
            }
        });
    }

    /**
     * Μέθοδος που εκκινεί την προηγούμενη δραστηριότητα.
     */
    private void back(){
        Intent intent = new Intent(this, activity_entrance.class);
        startActivity(intent);
    }


    /**
     * Μέθοδος που προχωρά στην επόμενη δραστηριότητα μετά την εγγραφή.
     */
    private void next(){

        Gson gson = new Gson();
        String json = gson.toJson(Account.accounts);
        editor.putString("data",json);
        editor.commit();
        Intent intent = new Intent(this, activity_entrance.class);
        intent.putExtra("Account", acc.getName());
        startActivity(intent);
    }
}
