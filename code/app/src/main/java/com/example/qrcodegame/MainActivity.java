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

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView welcomeText;
    TextView analyzeText;
    TextView resultText;
    ImageButton scanQRButton;

    Button locationPhotoBtn;
    Button saveQRtoCloudBtn;

    Button profileViewBtn;

    Button exploreMap;
    Button leaderboardBtn;

    CheckBox locationToggle;

    QRCode currentQRCode;
    byte[] locationImage;


    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    LocationHelper locationHelper;

    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;

    final FirebaseStorage storage = FirebaseStorage.getInstance();
    DocumentReference userDocument;
    CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("Codes");

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

        // Update
        welcomeText.setText("Welcome " + currentUserHelper.getUsername() + "!");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
                        locationImage = bytesStream.toByteArray();
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

        scanQRButton.setOnClickListener(this);

        locationPhotoBtn.setOnClickListener(view -> {

            // TODO
            // ADD CHECK HERE

            if (locationImage == null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
                return;
            }

            locationPhotoBtn.setText("TAKE PHOTO");
            locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
            Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show();
        });


        saveQRtoCloudBtn.setOnClickListener(v -> saveCode());

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

        currentQRCode = new QRCode();
        locationHelper = new LocationHelper(this);
        locationHelper.startLocationUpdates();

        resetUi();
    }

    /**
     * Initial step in saving code. It checks if the hash already exists.
     * If it does, it will update the info, else, creates the code.
     */
    private void saveCode() {
        userDocument = FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId());
        // Check if QR code already exists
        qrCollectionReference
            .whereEqualTo("id",currentQRCode.getId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    updateExistingCode();
                } else {
                    createNewCode();
                }
            });
    }

    /**
     * Create a new Hash object in db
     * Also stores the image if set.
     */
    private void createNewCode() {

        if (locationToggle.isChecked()) {
            currentQRCode.setCoordinates(currentUserHelper.getCurrentLocation());
        };

        if (currentQRCode.getId() == null || currentQRCode.getId().isEmpty() || currentQRCode.getWorth() == 0) {
            /*Toast.makeText(this, "Scan a QR Code first!", Toast.LENGTH_SHORT).show();
            return;*/

            // TODO
            // REMOVE
            currentQRCode.setId(UUID.randomUUID().toString());
            currentQRCode.setWorth((int) Math.floor(Math.random()*1000));
        };

        if (locationImage != null) {
            // Save Image
            StorageReference imageLocationStorage = storage.getReference().child("images").child(currentQRCode.getId() + ".jpg");
            imageLocationStorage
                    .putBytes(locationImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageLocationStorage.getDownloadUrl().addOnSuccessListener(uri -> {
                            currentQRCode.setImageUrl(uri.toString());
                            saveCodeFireStore();
                        }).addOnFailureListener(e -> {
                            System.err.println(e.getMessage());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image could not be saved!", Toast.LENGTH_SHORT).show();
                        System.err.println(e.getMessage());
                    });
        } else {
            saveCodeFireStore();
        }
    }

    /**
     * Updates the existing hash in DB
     */
    private void updateExistingCode() {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayUnion(currentQRCode.getId()));
        updates.put("totalScore", FieldValue.increment(currentQRCode.getWorth()));
        userDocument
            .update(updates);
        qrCollectionReference
            .document(currentQRCode.getId())
            .update("players", FieldValue.arrayUnion(currentUserHelper.getUsername()));
        resetUi();
    }

    /**
     * Resets the UI to match a new state.
     */
    private void resetUi() {
        Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
        currentQRCode = new QRCode();
        locationImage = null;
        locationPhotoBtn.setText("TAKE PHOTO");
        locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
        locationToggle.setChecked(false);
    }

    /**
     * Saves the code to the DB, then updates the player who saved it.
     */
    private void saveCodeFireStore() {
        currentQRCode.getPlayers().add(currentUserHelper.getUsername());
        qrCollectionReference
                .document(currentQRCode.getId())
                .set(currentQRCode)
                .addOnSuccessListener(v -> {
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("collectedCodes", FieldValue.arrayUnion(currentQRCode.getId()));
                    updates.put("totalScore", FieldValue.increment(currentQRCode.getWorth()));
                    userDocument.update(updates);
                    resetUi();
                });
    }


    /**
     * Event called implicitly! So don't call it yourself.
     * Opens QR scanner
     * @param view default view which was click.
     */
    @Override
    public void onClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    /**
     * Event called implicitly! So don't call it yourself.
     * Handles QR results!
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrCodeContent = result.getContents();
                handleHash(qrCodeContent);
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
    public void handleHash(String qrCodeContent) {

        int result = HashHelper.handleHash(this, currentQRCode, qrCodeContent);
        if (result == 1) {
            // Displaying text
            analyzeText.setVisibility(View.VISIBLE);
            resultText.setVisibility(View.VISIBLE);
            String message = "This Hash is worth: " + currentQRCode.getWorth();
            resultText.setText(message);
        }
        if (result == 2) { // This means we restarting!
            finish();
        }
    }

}