package com.example.qrcodegame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodegame.controllers.QRCodeController;
import com.example.qrcodegame.interfaces.CodeSavedListener;
import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.example.qrcodegame.utils.HashHelper;
import com.example.qrcodegame.utils.LocationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements CodeSavedListener {

    // View Elements
    TextView welcomeText;
    TextView analyzeText;
    TextView resultText;
    ImageButton scanQRButton;
    Button locationPhotoBtn;
    Button saveQRtoCloudBtn;
    ImageButton profileViewBtn;
    Button exploreMap;
    Button leaderboardBtn;
    CheckBox locationToggle;

    // Other needed variables
    QRCodeController qrCodeController;
    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    FusedLocationProviderClient fusedLocationProviderClient;
    ActivityResultLauncher<Intent> activityResultLauncher;


    /**
     * Binds views and attaches listeners
     * @param savedInstanceState android default
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        // Binding
        welcomeText = findViewById(R.id.welcomeText);
        analyzeText = findViewById(R.id.analyzeText);
        resultText = findViewById(R.id.codeWorthText);
        scanQRButton = findViewById(R.id.scanQRCodeBtn);
        locationPhotoBtn = findViewById(R.id.takeLocationBtn);
        saveQRtoCloudBtn = findViewById(R.id.saveQRtoCloudBtn);
        exploreMap = findViewById(R.id.exploreNearbyBtn);
        leaderboardBtn = findViewById(R.id.leaderboardBtn);
        locationToggle = findViewById(R.id.saveLocationCheckBox);
        profileViewBtn = findViewById(R.id.viewProfileBtn);

        // Update Title
        welcomeText.setText("Welcome " + currentUserHelper.getUsername() + "!");

        // Requesting permission
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        exploreMap.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MapsActivity.class)));

        // Updating listeners
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Fetching image
                        Bitmap image = (Bitmap) result.getData().getExtras().get("data");
                        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream);
                        qrCodeController.setLocationImage(bytesStream.toByteArray());
                        // Updating UI
                        locationPhotoBtn.setText("REMOVE IMAGE");
                        locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                    }
                }
        );

        // Listeners

        profileViewBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
                intent.putExtra("username", currentUserHelper.getUsername());
                startActivity(intent);
            });

        scanQRButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureAct.class);
            integrator.setOrientationLocked(true);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scanning Code");
            integrator.initiateScan();
        });

        locationPhotoBtn.setOnClickListener(view -> {

            // TODO
            // ADD CHECK HERE

            if (qrCodeController.getLocationImage() == null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
                return;
            }

            locationPhotoBtn.setText("TAKE PHOTO");
            locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
            Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show();
        });


        saveQRtoCloudBtn.setOnClickListener(v -> qrCodeController.saveCode(
                locationToggle.isChecked()
        ));

        leaderboardBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LeaderBoardActivity.class)));

    }

    /**
     * Updates the UI, as well starts location services
     */
    @Override
    protected void onStart() {
        super.onStart();

        analyzeText.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);

        qrCodeController = new QRCodeController(this, this);

//        currentQRCode.setCoordinates(currentUserHelper.getCurrentLocation());
//             String address = "";

//             Geocoder geocoder = new Geocoder(this, Locale.getDefault());

//             try {
//                 List<Address> listAddress = geocoder.getFromLocation(currentUserHelper.getCurrentLocation().get(0), currentUserHelper.getCurrentLocation().get(1), 1);
//                 if(listAddress != null && listAddress.size() > 0){
//                     if(listAddress.get(0).getLocality() != null){
//                         address += listAddress.get(0).getLocality()+", ";
//                     }
//                     if(listAddress.get(0).getAdminArea() != null){
//                         address += listAddress.get(0).getAdminArea()+", ";
//                     }
//                     if(listAddress.get(0).getCountryName() != null){
//                         address += listAddress.get(0).getCountryName()+", ";
//                     }
//                 }
//             }catch (Exception e){
//                 e.printStackTrace();
//             }
//             currentQRCode.setAddress(address);
      
      
        resetUI();
    }

    /**
     * Resets the UI to match a new state.
     */
    public void resetUI() {
        locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
        locationToggle.setChecked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrCodeContent = result.getContents();
                processHashFrontEnd(qrCodeContent);
            } else {
                Toast.makeText(this, "Scanning cancelled!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Handles the hash results from scanning!
     * It will update the QR code with scores. Display the worth on UI
     * It will also handle special QR code commands like view-profiles, and switch-profiles
     * @param qrCodeContent String which was found in the QR code
     */
    public void processHashFrontEnd(String qrCodeContent) {
        int result = qrCodeController.processHash(qrCodeContent);
        if (result == 1) {
            // Displaying text
            analyzeText.setVisibility(View.VISIBLE);
            resultText.setVisibility(View.VISIBLE);
            String message = "This Hash is worth: " + qrCodeController.getCurrentQrCode().getWorth();
            resultText.setText(message);
        }
        if (result == 2) {
            // This means we restarting!
            finish();
        }
    }

}