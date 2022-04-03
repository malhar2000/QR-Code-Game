package com.example.qrcodegame;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.adapters.OwnerQRCodeAdapter;
import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.QRCode;

import java.util.ArrayList;
import java.util.Objects;


/**
 * QR activity for owner to delete QR codes.
 */
public class OwnerQRActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OwnerQRCodeAdapter ownerQRCodeAdapter;
    private ArrayList<QRCode> allQRCodes;
    private final FireStoreController fireStoreController = FireStoreController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_owner_qractivity);
        recyclerView = findViewById(R.id.allQRCodesOwner);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        allQRCodes = new ArrayList<>();

        initQRCodes();

    }

    private void initQRCodes() {
        fireStoreController.getAllQRCodes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> allQRCodes.add(documentSnapshot.toObject(QRCode.class)));
                    initList();
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void initList() {
        ownerQRCodeAdapter = new OwnerQRCodeAdapter(allQRCodes);
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
                        ownerQRCodeAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }
    };
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }

}