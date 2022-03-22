package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.qrcodegame.adapters.OwnerQRCodeAdapter;
import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.QRCode;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


public class OwnerQRActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OwnerQRCodeAdapter ownerQRCodeAdapter;
    private ArrayList<QRCode> allQRCodes;
    private FireStoreController fireStoreController = FireStoreController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_qractivity);
        recyclerView = findViewById(R.id.allQRCodesOwner);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        allQRCodes = new ArrayList<>();

        initQRCodes();

    }

    private void initQRCodes() {
        fireStoreController.getAllQRCodes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> {
                        allQRCodes.add(documentSnapshot.toObject(QRCode.class));
                    });
                    initList();
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private void initList() {
        ownerQRCodeAdapter = new OwnerQRCodeAdapter(allQRCodes, this);
        recyclerView.setAdapter(ownerQRCodeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            QRCode qrCodeToDelete = allQRCodes.get(viewHolder.getLayoutPosition());
            fireStoreController.deleteQRCode(qrCodeToDelete)
                    .addOnSuccessListener(v -> {
                        allQRCodes.remove(qrCodeToDelete);
                        ownerQRCodeAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> e.printStackTrace());
        }
    };

}