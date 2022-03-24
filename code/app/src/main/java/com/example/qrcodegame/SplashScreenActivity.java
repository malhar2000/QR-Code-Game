package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

// splash screen
// no issues
public class SplashScreenActivity extends AppCompatActivity {

    // FOR DEVELOPMENT PURPOSES
    private boolean APP_IN_TEST_MODE = false;

    private final FireStoreController fireStoreController = FireStoreController.getInstance();
    private CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Objects.requireNonNull(getSupportActionBar()).hide();
    }

    /**
     * Fetches user info from DB. If the user is not found, it will redirect to the sign-up page. Else, it will take them to the correct home page depending on admin status.
     */
    @Override
    protected void onStart() {
        super.onStart();

        Thread backgroundThread = new Thread() {
            @Override
            public void run() {
                try {

                    // Update the central user helper
                    currentUserHelper.setUniqueID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                    // Checking in db if this user exists
                    // Furthermore, fetch whether or not this user is a admin or a regular user
                    fireStoreController.findUserBasedOnDeviceId()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            // Check if the user already exists
                            List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                            // If no users found
                            if (results.isEmpty()) {
                                // Start a new user activity
                                System.out.println("NEW ACTIVITY: NEW USER");
                                Intent intent = new Intent(SplashScreenActivity.this, FirstTimeActivity.class);
                                startActivity(intent);
                                finish();
                                // Just in-case this hasn't finished
                                return;
                            }
                            // This means there are users found, lets get the first user
                            User currentUser = results.get(0).toObject(User.class);
                            currentUserHelper.setUsername(Objects.requireNonNull(currentUser).getUsername());
                            currentUserHelper.setOwner(currentUser.getIsOwner());
                            currentUserHelper.setFirebaseId(currentUser.getUsername());
                            currentUserHelper.setEmail(currentUser.getEmail());
                            currentUserHelper.setPhone(currentUser.getPhone());
                            currentUserHelper.setAppInTestMode(APP_IN_TEST_MODE);

                            // Switch to the correct activity.
                            Intent intent;
                            if (currentUser.getIsOwner()) {
                                // If owner, start the owner views
                                intent = new Intent(SplashScreenActivity.this, OwnerHomeActivity.class);
                                Log.d("New activity", "Switching to owner activity as " + currentUser.getUsername());
                            } else {
                                // Else start the regular user views
                                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                Log.d("New activity", "Switching to regular activity as " + currentUser.getUsername());
                            }

                            // Switch
                            startActivity(intent);
                            finish();
                        });

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        backgroundThread.start();
    }
}