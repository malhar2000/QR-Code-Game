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
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.example.qrcodegame.utils.LocationHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationHelper.handleLocationChanged {

    TextView welcomeText;
    TextView analyzeText;
    TextView resultText;
    ImageButton scanQRButton;

    Button locationPhotoBtn;
    Button saveQRtoCloudBtn;

    QRCode currentQRCode;
    byte[] locationImage;

    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    LocationHelper locationHelper;

    final FirebaseStorage storage = FirebaseStorage.getInstance();
    DocumentReference userDocument;
    CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("Codes");

    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Permission

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        };

        // Binding
        welcomeText = findViewById(R.id.welcomeText);
        analyzeText = findViewById(R.id.analyzeText);
        resultText = findViewById(R.id.codeWorthText);
        scanQRButton = findViewById(R.id.scanQRCodeBtn);
        locationPhotoBtn = findViewById(R.id.takeLocationBtn);
        saveQRtoCloudBtn = findViewById(R.id.saveQRtoCloudBtn);

        // Update
        welcomeText.setText("Welcome " + currentUserHelper.getUsername() + "!");
        analyzeText.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);

        currentQRCode = new QRCode();
        locationHelper = new LocationHelper(this, currentQRCode, this);

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
        scanQRButton.setOnClickListener(this);

        locationPhotoBtn.setOnClickListener(view -> {

            if (currentQRCode.getId() == null || currentQRCode.getId().isEmpty() || currentQRCode.getWorth() == 0) {
                Toast.makeText(this, "Scan a QR Code first!", Toast.LENGTH_SHORT).show();
                return;
            };

            if (locationImage == null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
                return;
            }

            locationPhotoBtn.setText("TAKE PHOTO");
            locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
            Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show();
        });

        saveQRtoCloudBtn.setOnClickListener(v -> {
            saveCode();
        });

        ((CheckBox) findViewById(R.id.saveLocationCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveQRtoCloudBtn.setText("WAIT...");
                    saveQRtoCloudBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                    locationHelper.getCurrentLocation();
                } else {
                    currentQRCode.getCoordinates().clear();
                }
            }
        });

    };



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

    private void createNewCode() {

        boolean saveLocation = ((CheckBox) findViewById(R.id.saveLocationCheckBox)).isChecked();

        if (saveLocation && currentQRCode.getCoordinates().size() == 0) {
            System.out.println("RETRY!");
            return;
        };

        if (currentQRCode.getId() == null || currentQRCode.getId().isEmpty() || currentQRCode.getWorth() == 0) {
            Toast.makeText(this, "Scan a QR Code first!", Toast.LENGTH_SHORT).show();
            return;
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

    private void resetUi() {
        Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
        currentQRCode = new QRCode();
        locationHelper.setCurrentQRCode(currentQRCode);
        locationImage = null;
        locationPhotoBtn.setText("TAKE PHOTO");
        locationPhotoBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
        ((CheckBox) findViewById(R.id.saveLocationCheckBox)).setChecked(false);
    }

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


    @Override
    public void onLocationReady() {
        saveQRtoCloudBtn.setText("ADD QR CODE");
        saveQRtoCloudBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
    }


    @Override
    public void onClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrCodeContent = result.getContents();
                calculateWorth(qrCodeContent);
            } else {
                Toast.makeText(this, "Scanning cancelled!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void calculateWorth(String qrCodeContent) {

        if (qrCodeContent == null || qrCodeContent.isEmpty()) {
            return;
        }

        if (qrCodeContent.startsWith("Account-Transfer=")) {
            // Transfer account settings
            return;
        }

        if (qrCodeContent.startsWith("View-Profile=")) {
            // View someones profile
            return;
        }

        try {

            // calculate sha-256
            // Citation: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(qrCodeContent.getBytes(StandardCharsets.UTF_8));

            final StringBuilder hashStr = new StringBuilder(hash.length);
            for (byte hashByte : hash)
                hashStr.append(Integer.toHexString(255 & hashByte));

            // Hashed string here
            final String hashedContent = hashStr.toString();


            final char[] hashedArray = hashedContent.toCharArray();

            // Calculating String
            int codeWorth = 0;
            char lastChar = hashedArray[0];
            int count = 0;
            for (char c : hashedArray) {
                if (c == lastChar) {
                    count++;
                } else {
                    codeWorth += ((int) c) * count;
                    lastChar = c;
                    count = 0;
                }
            }

            // Displaying text
            analyzeText.setVisibility(View.VISIBLE);
            resultText.setVisibility(View.VISIBLE);
            resultText.setText("This Hash is worth: " + codeWorth);

            // Updating the QR code Object
            currentQRCode.setId(hashedContent);
            currentQRCode.setWorth(codeWorth);


        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }

    }

}