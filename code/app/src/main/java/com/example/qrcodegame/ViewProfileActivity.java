package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.qrcodegame.adapters.qrCodeRecyclerViewAdapter;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.example.qrcodegame.utils.QRCodeDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ViewProfileActivity extends AppCompatActivity implements qrCodeRecyclerViewAdapter.QRProfileListener {

    private ArrayList<String> qrCodeNames;
    private ArrayList<String> qrCodeScores;
    private TextView txtViewTotalScore;
    private TextView txtViewTotalCodes;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    private int totalScore;
    private Button btnOpenQRCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

    }

    @Override
    protected void onStart() {
        super.onStart();
        qrCodeNames = new ArrayList<String>();
        qrCodeScores = new ArrayList<String>();
        Button btnEditProfile = findViewById(R.id.buttonEditProfile);
        Button btnOpenQRCode = findViewById(R.id.buttonOpenQRCode);
        txtViewTotalCodes = findViewById(R.id.textViewTotalCodes);
        txtViewTotalScore = findViewById(R.id.textViewTotalScore);
        totalScore = 0;


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String username = (String) intent.getExtras().get("username");
        Objects.requireNonNull(getSupportActionBar()).setTitle(username+"'s " + "profile");
        if (!username.equals(currentUserHelper.getUsername()))
        {
            btnEditProfile.setVisibility(View.INVISIBLE);
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ///////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////


                            /// MAKE INTENT TO CONTACT PROFILE EDIT PAGE HERE ////////

                ///////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////
            }
        });

        btnOpenQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(username);
            }
        });
        fetchQRCodesOfUser(username);
    }

    public void openDialog(String username) {
        Intent intent = new Intent(this, QRCodeDialog.class);
        intent.putExtra("the username", username);
        Bundle args = (intent.getExtras());

        QRCodeDialog qrCodeDialog = new QRCodeDialog();
        qrCodeDialog.setArguments(args);
        qrCodeDialog.show(getSupportFragmentManager(), "qrCode dialog");
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.qrCodeRecyclerView);
        qrCodeRecyclerViewAdapter adapter = new qrCodeRecyclerViewAdapter(qrCodeNames, qrCodeScores, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void fetchQRCodesOfUser(String username) {
        db.collection("Codes").whereArrayContains("players", username)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot existingUser : queryDocumentSnapshots) {
                        qrCodeNames.add("" + existingUser.get("id"));
                        qrCodeScores.add("Score: "+existingUser.get("worth").toString());
                        totalScore += Integer.parseInt(existingUser.get("worth").toString());
                    }
                    initRecyclerView();
                    txtViewTotalScore.setText("Total score: " + totalScore);
                    txtViewTotalCodes.setText("Total number of codes scanned: " + qrCodeNames.size());
                }
            });
    }

    @Override
    public void onQRclicked(String qrId) {
            Intent intent = new Intent(ViewProfileActivity.this , SingleQRActivity.class);
            intent.putExtra("codeID", qrId);
            startActivity(intent);
    }
}