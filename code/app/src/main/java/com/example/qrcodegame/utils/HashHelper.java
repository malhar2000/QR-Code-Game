package com.example.qrcodegame.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.qrcodegame.SplashScreenActivity;
import com.example.qrcodegame.ViewProfileActivity;
import com.example.qrcodegame.models.QRCode;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HashHelper {

    public static int handleHash(Context context, QRCode currentQRCode, String qrCodeContent) {

        if (qrCodeContent == null || qrCodeContent.isEmpty()) {
            return 0;
        }

        if (qrCodeContent.startsWith("View-Profile=")) {
            // View someones profile
            String usernameToView = qrCodeContent.split("=")[1];
            Intent intent = new Intent(context, ViewProfileActivity.class);
            intent.putExtra("username", usernameToView);
            return 0;
        }

        if (qrCodeContent.startsWith("Transfer-Profile=")) {
            AtomicInteger success = new AtomicInteger();
            //
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("devices", FieldValue.arrayUnion(CurrentUserHelper.getInstance().getUniqueID()));
            // Transfer
            String usernameToTransferTo = qrCodeContent.split("=")[1];
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(usernameToTransferTo)
                    .update(updates)
                    .addOnSuccessListener(v -> {
                        FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(CurrentUserHelper.getInstance().getFirebaseId())
                                .update("devices", FieldValue.arrayRemove(CurrentUserHelper.getInstance().getUniqueID()))
                                .addOnSuccessListener(v1 -> {
                                    Intent intent = new Intent(context, SplashScreenActivity.class);
                                    context.startActivity(intent);
                                    success.set(2);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show();
                    });
            return success.get();
        }

        calculateWorth(context, currentQRCode, qrCodeContent);
        return 1;
    }

    public static void calculateWorth(Context context, QRCode currentQRCode, String qrCodeContent) {
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
            currentQRCode.setId(hashedContent);
            currentQRCode.setWorth(codeWorth);

        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            System.err.println(e.getMessage());
        }
    }

}
