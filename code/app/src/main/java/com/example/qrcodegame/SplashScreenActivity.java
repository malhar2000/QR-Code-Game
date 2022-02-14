package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;

import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SplashScreenActivity extends AppCompatActivity {

    private String android_id;
    private CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread backgroundThread = new Thread() {
            @Override
            public void run() {
                try {
                    // Fetching the device's unique ID
                    android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                    // Checking in db if this user exists
                    // Furthermore, fetch whether or not this user is a admin or a regular user
//                    userCollectionReference



                } catch (Exception e) {

                } finally {

                }
            }
        };

        backgroundThread.start();
    }
}