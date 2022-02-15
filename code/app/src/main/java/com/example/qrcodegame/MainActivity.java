package com.example.qrcodegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView welcomeText;
    TextView analyzeText;
    TextView resultText;
    ImageButton scanQRButton;
    String qrCodeContent;
    int codeWorth;
    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Binding
        welcomeText = findViewById(R.id.welcomeText);
        analyzeText = findViewById(R.id.analyzeText);
        resultText = findViewById(R.id.codeWorthText);
        scanQRButton = findViewById(R.id.scanQRCodeBtn);

        // Update
        welcomeText.setText("Welcome " + currentUserHelper.getUsername() +"!");
        analyzeText.setVisibility(View.INVISIBLE);
        resultText.setVisibility(View.INVISIBLE);

        // Listeners
        scanQRButton.setOnClickListener(this);

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
                qrCodeContent =  result.getContents();
                calculateWorth();
                return;
            } else {
                Toast.makeText(this, "Scanning cancelled!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void calculateWorth() {

        if (qrCodeContent == null || qrCodeContent.isEmpty()) {
            return;
        }

        if (qrCodeContent.startsWith("Account-Transfer=")){
            // Transfer account settings
            return;
        }

        if (qrCodeContent.startsWith("View-Profile=")){
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

            System.out.println("HERE " + hashedContent);

            // Calculating String
            codeWorth = 0;
            char lastChar = hashedArray[0];
            int count = 0;
            for (char c: hashedArray) {
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

        } catch (Exception e) {
            codeWorth = -1;
            System.err.println(e.getMessage());
            return;
        }

    }

}