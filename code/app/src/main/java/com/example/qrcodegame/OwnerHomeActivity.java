package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

// yet to be completed
public class OwnerHomeActivity extends AppCompatActivity {

    private Button btnViewPlayers;
    private Button btnViewQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        btnViewPlayers = findViewById(R.id.viewOrEditPlayer);
        btnViewQR = findViewById(R.id.viewOrEditQR);

        btnViewPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerHomeActivity.this, OwnerViewPlayerActivity.class);
                startActivity(intent);
            }
        });

        btnViewQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerHomeActivity.this, OwnerQRActivity.class);
                startActivity(intent);
            }
        });

    }



}