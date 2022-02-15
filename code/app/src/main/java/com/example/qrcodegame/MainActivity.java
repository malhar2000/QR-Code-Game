package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.qrcodegame.utils.CurrentUserHelper;

public class MainActivity extends AppCompatActivity {

    TextView welcomeText;
    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Bind Texts
        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + currentUserHelper.getUsername() +"!");


    }
}