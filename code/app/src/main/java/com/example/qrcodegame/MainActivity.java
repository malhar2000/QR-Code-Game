package com.example.qrcodegame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.qrcodegame.controllers.QRCodeController;
import com.example.qrcodegame.interfaces.CodeSavedListener;
import com.example.qrcodegame.interfaces.OnProfileTransferedListener;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * Main Activity!
 * No issues
 */
public class MainActivity extends AppCompatActivity implements CodeSavedListener, OnProfileTransferedListener {

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

        //BottomNavigationView navigationView = findViewById(R.id.bottom_nav);

        // Update Title
        welcomeText.setText("Welcome " + currentUserHelper.getUsername() + "!");


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

            if (!currentUserHelper.getIsAppInTestMode() && qrCodeController.getCurrentQrCode().getId() == null) {
                Toast.makeText(this, "Scan a QR code first!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (qrCodeController.getLocationImage() == null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
                return;
            }

            locationPhotoBtn.setText("TAKE PHOTO");
            qrCodeController.setLocationImage(null);
            locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
            Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show();
        });


        saveQRtoCloudBtn.setOnClickListener(v -> {

            // Check if valid QR Code
            if (qrCodeController.getCurrentQrCode().getId() == null || qrCodeController.getCurrentQrCode().getId().isEmpty() || qrCodeController.getCurrentQrCode().getWorth() == 0) {
                if (currentUserHelper.getIsAppInTestMode()) {
                    qrCodeController.getCurrentQrCode().setId(UUID.randomUUID().toString());
                    qrCodeController.getCurrentQrCode().setWorth((int) Math.floor(Math.random()*1000));
                } else {
                    Toast.makeText(this, "Scan a QR Code first!", Toast.LENGTH_SHORT).show();
                    return;
                }
            };

            qrCodeController.saveCode(locationToggle.isChecked());
            saveQRtoCloudBtn.setText("WAIT...");
            saveQRtoCloudBtn.setClickable(false);
        });


        leaderboardBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LeaderBoardActivity.class)));


        qrCodeController = new QRCodeController(this, this, this);
        resetUI();

    }


    /**
     * Resets the UI to match a new state.
     */
    public void resetUI() {
        analyzeText.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);
        locationPhotoBtn.setText("TAKE PHOTO");
        saveQRtoCloudBtn.setText("ADD QR CODE");
        locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
        locationToggle.setChecked(false);
        saveQRtoCloudBtn.setClickable(true);
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
            System.out.println(qrCodeController.getCurrentQrCode().getWorth());
            String message = "This Hash is worth: " + qrCodeController.getCurrentQrCode().getWorth();
            resultText.setText(message);
            System.out.println("TEXT " + resultText.getText().toString());
        }
    }

    @Override
    public void OnProfileTransfered() {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);
            finish();
    }
}