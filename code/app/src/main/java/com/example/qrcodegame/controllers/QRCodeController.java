package com.example.qrcodegame.controllers;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.qrcodegame.ViewProfileActivity;
import com.example.qrcodegame.interfaces.CodeSavedListener;
import com.example.qrcodegame.interfaces.OnProfileTransferedListener;
import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.example.qrcodegame.utils.LocationHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Aids in getting and setting QR code related data
 * No Issues
 */
public class QRCodeController {

    private QRCode currentQrCode;
    private Context context;
    public byte[] locationImage;
    private CodeSavedListener codeSavedListener;
    private OnProfileTransferedListener onProfileTransferedListener;

    // Helper Var
    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    // Firestore Variables
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FireStoreController fireStoreController = FireStoreController.getInstance();

    /**
     * Resets the code and image data
     */
    public void initNewCode(){
        currentQrCode = new QRCode();
        locationImage = null;
    }

    public QRCodeController(Context context, CodeSavedListener codeSavedListener, OnProfileTransferedListener onProfileTransferedListener) {
        this.context = context;
        currentQrCode = new QRCode();
        // Variables we use
        LocationHelper locationHelper = new LocationHelper(context);
        locationHelper.startLocationUpdates();
        this.codeSavedListener = codeSavedListener;
        this.onProfileTransferedListener = onProfileTransferedListener;
    }

    /**
     * This method handles the hash content and takes the correct next steps if needed
     * @param qrCodeContent The String stored within the QR code
     * @return status code
     */
    public int processHash(String qrCodeContent) {
        CollectionReference userCollection = FirebaseFirestore.getInstance().collection("Users");

        if (qrCodeContent == null || qrCodeContent.isEmpty()) {
            return 0;
        }

        if (qrCodeContent.startsWith("View-Profile=")) {
            // View someones profile
            String usernameToView = qrCodeContent.split("=")[1];
            Intent intent = new Intent(context, ViewProfileActivity.class);
            intent.putExtra("username", usernameToView);
            context.startActivity(intent);
            return 0;
        }

        if (qrCodeContent.startsWith("Transfer-Profile=")) {
            String usernameToTransferTo = qrCodeContent.split("=")[1];
            fireStoreController.switchProfile(usernameToTransferTo)
                    .addOnSuccessListener(unused -> onProfileTransferedListener.OnProfileTransfered())
                    .addOnFailureListener(Throwable::printStackTrace);
            return 2;
        }

        calculateWorth(qrCodeContent);
        return 1;
    }

    /**
     * Calculates the worth of the code.
     * @param qrCodeContent String content of the hash scanned
     */
    public void calculateWorth(String qrCodeContent) {
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
            char[] codeArray = new char[hashedContent.length()];
            int[] intCodeArray = new int[hashedContent.length() + 1];
            int comparer = 0;
            int codeWorth = 0;

            int counter = 0;

            // Copy char by char into array
            for (int i = 0; i < hashedContent.length(); i++) {
                codeArray[i] = hashedContent.charAt(i);
            }

            // hex to int. put this int in the intCodeArray
            // help from https://stackoverflow.com/questions/26839558/hex-char-to-int-conversion
            for (int i = 0; i < hashedContent.length(); i++) {
                if (codeArray[i] >= '0' && codeArray[i] <= '9')
                    intCodeArray[i] = codeArray[i] - '0';
                if (codeArray[i] >= 'A' && codeArray[i] <= 'F')
                    intCodeArray[i] = codeArray[i] - 'A' + 10;
                if (codeArray[i] >= 'a' && codeArray[i] <= 'f')
                    intCodeArray[i] = codeArray[i] - 'a' + 10;
            }

            // used for cases like "1000"; the 16 at the end breaks the counter since its the
            // end of the hash string. And 16 is out of the range of a hex so its safe to use
            intCodeArray[hashedContent.length()] = 16;

            comparer = intCodeArray[0];
            for (int i = 1; i < hashedContent.length() + 1; i++) {
                if (intCodeArray[i] == comparer) {
                    counter += 1;
                } else {
                    if (counter != 0) {
                        if (comparer != 0) {
                            codeWorth += Math.pow(comparer, counter);
                        } else {
                            codeWorth += Math.pow(20, counter);
                        }
                        counter = 0;
                    } else if (comparer == 0) {
                        codeWorth += 1;
                    }
                    comparer = intCodeArray[i];
                }
            }

            // Updating the QR code Object
            currentQrCode.setId(hashedContent);
            currentQrCode.setWorth(codeWorth);

        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initial step in saving code. It checks if the hash already exists.
     * If it does, it will update the info, else, creates the code.
     */
    public void saveCode(Boolean locationIsChecked) {
        // Check if QR code already exists
        fireStoreController.checkQRExists(currentQrCode.getId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        updateExistingCode();
                    } else {
                        createNewCode(locationIsChecked);
                    }
                });
    }


    /**
     * Create a new Hash object in db
     * Also stores the image if set.
     */
    private void createNewCode(Boolean locationIsChecked) {
        // Add location if checked and location is ready
        if (locationIsChecked && !Objects.isNull(currentUserHelper.getCurrentLocation()) && currentUserHelper.getCurrentLocation().size() > 0 ) {
            addLocationThingsToQrCode();
        };

        if (currentQrCode.getId() == null || currentQrCode.getId().isEmpty() || currentQrCode.getWorth() == 0) {
            /*Toast.makeText(this, "Scan a QR Code first!", Toast.LENGTH_SHORT).show();
            return;*/
            // TODO
            // REMOVE
            currentQrCode.setId(UUID.randomUUID().toString());
            currentQrCode.setWorth((int) Math.floor(Math.random()*1000));
        };

        if (locationImage != null) {
            // Save Image
            StorageReference imageLocationStorage = storage.getReference().child("images").child(currentQrCode.getId() + ".jpg");
            imageLocationStorage
                    .putBytes(locationImage)
                    .addOnSuccessListener(taskSnapshot -> imageLocationStorage.getDownloadUrl().addOnSuccessListener(uri -> {
                        currentQrCode.setImageUrl(uri.toString());
                        saveCodeFireStore();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Image could not be saved!", Toast.LENGTH_SHORT).show();
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
        fireStoreController.updateExistingQrCode(currentQrCode)
                .addOnSuccessListener(v -> {
                    initNewCode();
                    Toast.makeText(context, "Stored!", Toast.LENGTH_SHORT).show();
                    codeSavedListener.resetUI();
                });
    }

    /**
     * Saves the code to the DB, then updates the player who saved it.
     */
    private void saveCodeFireStore() {
        currentQrCode.getPlayers().add(currentUserHelper.getUsername());
        fireStoreController.saveNewQrCode(currentQrCode)
                .addOnSuccessListener(v -> {
                    initNewCode();
                    Toast.makeText(context, "Created!", Toast.LENGTH_SHORT).show();
                    codeSavedListener.resetUI();
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public QRCode getCurrentQrCode() {
        return currentQrCode;
    }

    public void setCurrentQrCode(QRCode currentQrCode) {
        this.currentQrCode = currentQrCode;
    }

    public byte[] getLocationImage() {
        return locationImage;
    }

    public void setLocationImage(byte[] locationImage) {
        this.locationImage = locationImage;
    }

    /**
     * Adds location and address to qr code
     */
    private void addLocationThingsToQrCode() {
        currentQrCode.setCoordinates(currentUserHelper.getCurrentLocation());
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> listAddress = geocoder.getFromLocation(currentUserHelper.getCurrentLocation().get(0), currentUserHelper.getCurrentLocation().get(1), 1);
            if (listAddress != null && listAddress.size() > 0) {
                if (listAddress.get(0).getLocality() != null) {
                    address = listAddress.get(0).getLocality() + ", ";
                }
                if (listAddress.get(0).getAdminArea() != null) {
                    address += listAddress.get(0).getAdminArea() + ", ";
                }
                if (listAddress.get(0).getCountryName() != null) {
                    address += listAddress.get(0).getCountryName() + ", ";
                }
            }
            currentQrCode.setAddress(address);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
