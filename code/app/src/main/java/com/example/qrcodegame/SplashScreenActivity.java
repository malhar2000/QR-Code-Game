package com.example.qrcodegame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.AwaitingPermissionsHelper;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.AllPermission;
import java.util.List;
import java.util.Objects;

// splash screen
// no issues
public class SplashScreenActivity extends AppCompatActivity implements AwaitingPermissionsHelper.AllVariablesTrueListener {

    // FOR DEVELOPMENT PURPOSES
    private final boolean APP_IN_TEST_MODE = true;

    // Helpers
    private final FireStoreController fireStoreController = FireStoreController.getInstance();
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    private final AwaitingPermissionsHelper awaitingPermissionsHelper = new AwaitingPermissionsHelper(this);

    // UI
    Button locationReqBtn, cameraReqBtn;

    // Others
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            awaitingPermissionsHelper.setLocationGranted(true);
                            toggleButton(locationReqBtn, "LOCATION");
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            Toast.makeText(this, "Grant Precise Location!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Grant Location!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    ActivityResultLauncher<String[]> cameraPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                        if (cameraGranted) {
                            awaitingPermissionsHelper.setCameraGranted(true);
                            toggleButton(cameraReqBtn, "CAMERA");
                        } else {
                            Toast.makeText(this, "Grant Camera!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        locationReqBtn = findViewById(R.id.reqLocationBtn);
        cameraReqBtn = findViewById(R.id.reqCameraBtn);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            awaitingPermissionsHelper.setLocationGranted(true);
            toggleButton(locationReqBtn, "LOCATION");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            awaitingPermissionsHelper.setCameraGranted(true);
            toggleButton(cameraReqBtn, "CAMERA");
        }

        locationReqBtn.setOnClickListener(view -> locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }));


        cameraReqBtn.setOnClickListener(view -> cameraPermissionRequest.launch(new String[] {
                Manifest.permission.CAMERA,
        }));

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchUserInfoFromDB();
    }

    private void switchActivityKnownUser() {
        Intent intent;
        if (currentUserHelper.getOwner()) {
            // If owner, start the owner views
            intent = new Intent(SplashScreenActivity.this, OwnerHomeActivity.class);
            Log.d("New activity", "Switching to owner activity as " + currentUserHelper.getUsername());
        } else {
            // Else start the regular user views
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            Log.d("New activity", "Switching to regular activity as " + currentUserHelper.getUsername());
        }
        // Switch
        startActivity(intent);
        finish();
    }
    private void switchActivityNewUser() {
        System.out.println("NEW ACTIVITY: NEW USER");
        Intent intent = new Intent(SplashScreenActivity.this, FirstTimeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches user info from DB. If the user is not found, it will redirect to the sign-up page. Else, it will update the waiting variables.
     */
    private void fetchUserInfoFromDB() {
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
                    awaitingPermissionsHelper.setUserFoundInDB(2);
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

                awaitingPermissionsHelper.setUserFoundInDB(1);
            });
    }

    @Override
    public void onAllVariablesTrue() {
        if (awaitingPermissionsHelper.isUserFoundInDB() == 1) {
            switchActivityKnownUser();
        } else {
            switchActivityNewUser();
        }
    }

    private void toggleButton(Button b, String type) {
        if (b.isEnabled()) {
            b.setEnabled(false);
            b.setBackgroundColor(0xFF00FF00);
            b.setText("Granted");
        } else {
            b.setEnabled(false);
            b.setBackgroundColor(0xFF0000FF);
            b.setText("GRANT " + type);
        }
    }

}