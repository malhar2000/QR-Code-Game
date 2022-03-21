package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.renderscript.Element;
import android.view.View;
import android.widget.Button;

import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class OwnerViewPlayerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
//    DatabaseReference database;
    FireStoreController fireStoreController = FireStoreController.getInstance();
    playerAdapter Player_adapter;
    ArrayList<User> listOfUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_view_player);

        recyclerView = findViewById(R.id.playerList);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listOfUsers = new ArrayList<>();
        Player_adapter = new playerAdapter(this,listOfUsers);

        recyclerView.setAdapter(Player_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fireStoreController.getAllUsers().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                listOfUsers.add(d.toObject(User.class));
            }
            Player_adapter.notifyDataSetChanged();



        });

//        Button btn_delete;
//        btn_delete = findViewById(R.id.delete_btn);
//        btn_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                fireStoreController.getAllUsers().addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
//                        listOfUsers.remove(d.toObject(User.class));
//                    }
//
//            });
//        }
//
//
//    });
}}
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Player_data player_data = dataSnapshot.getValue(Player_data.class);
//                    list.add(player_data);
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
