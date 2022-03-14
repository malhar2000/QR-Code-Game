package com.example.qrcodegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodegame.adapters.LeaderBoardAdapter;
import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.controllers.LeaderBoardController;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// This is the leaderboard card view where users can see each others and their ranking
// no issues
public class LeaderBoardActivity extends AppCompatActivity{

    // View elements
    LeaderBoardAdapter adapter;
    RecyclerView recyclerView;
    TextView myScoreByRank;
    TextView myScoreByCode;
    Button backBtn;
    EditText searchPlayer;

    // Other things we need
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    private final FireStoreController fireStoreController = FireStoreController.getInstance();
    private final LeaderBoardController leaderBoardController = new LeaderBoardController();

    // Storing Data for activity
    private ArrayList<User> allPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        setup();

        myScoreByCode = findViewById(R.id.editTextRankByScanned);
        myScoreByRank = findViewById(R.id.editTextRankByScore);
        searchPlayer = findViewById(R.id.editTextSearchForPlayers);

        searchPlayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        backBtn = findViewById(R.id.backButtonLeaderBoard);
        backBtn.setOnClickListener(view -> finish());

    }

    private void filter(String text){
        ArrayList<User> fList = new ArrayList<>();
        for(User u : allPlayers){
            if(u.getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                fList.add(u);
            }
        }
        adapter.filterList(fList);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycle_view);
        adapter = new LeaderBoardAdapter(allPlayers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setup(){

        allPlayers.clear();
        fireStoreController.getAllPlayers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Adding users to list
                    queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> {
                        allPlayers.add(documentSnapshot.toObject(User.class));
                    });

                    // Set Ranks
                    int rankByNumOfCodes = leaderBoardController.getScoreByNumberOfQrScanned(allPlayers);
                    int rankByScore = leaderBoardController.getScoreByRank(allPlayers);
                    myScoreByCode.setText( rankByNumOfCodes != -1 ? "" + rankByNumOfCodes : "Not-Found!");
                    myScoreByRank.setText( rankByScore != -1 ? "" + rankByScore : "Not-Found!");

                    initRecyclerView();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                });


    }

}