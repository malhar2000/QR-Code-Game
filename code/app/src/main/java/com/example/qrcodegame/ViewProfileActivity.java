package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.qrcodegame.adapters.qrCodeRecyclerViewAdapter;
import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.example.qrcodegame.utils.QRCodeDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * what a user sees when they view another users profile
 * no issues
 */
public class ViewProfileActivity extends AppCompatActivity implements qrCodeRecyclerViewAdapter.QRProfileListener {

    private ArrayList<QRCode> qrCodes;
    private qrCodeRecyclerViewAdapter adapter;
    private TextView txtViewTotalScore;
    private TextView txtViewTotalCodes;
    private final FireStoreController fireStoreController = FireStoreController.getInstance();
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    private int totalScore;
    private Button btnOpenQRCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        qrCodes = new ArrayList<>();
        ImageButton btnEditProfile = findViewById(R.id.buttonEditProfile);
        btnOpenQRCode = findViewById(R.id.buttonOpenShareQRCode);
        txtViewTotalCodes = findViewById(R.id.textViewTotalCodes);
        txtViewTotalScore = findViewById(R.id.textViewTotalScore);
        totalScore = 0;

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String username = (String) intent.getExtras().get("username");
        Objects.requireNonNull(getSupportActionBar()).setTitle(username+"'s " + "profile");
        if (!username.equals(currentUserHelper.getUsername())) {
            btnEditProfile.setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.profileTransferBtn)).setVisibility(View.INVISIBLE);
        }

        btnEditProfile.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));

        btnOpenQRCode.setOnClickListener(v -> openDialog("View-Profile=" + username, "Share your Profile! (Not Transfer)"));

        ((Button) findViewById(R.id.profileTransferBtn)).setOnClickListener(v -> openDialog("Transfer-Profile=" + username, "Transfer your Profile!"));

        fetchQRCodesOfUser(username);
    }

    public void openDialog(String message, String title) {
        Intent intent = new Intent(this, QRCodeDialog.class);
        intent.putExtra("content", message);
        intent.putExtra("title", title);
        Bundle args = (intent.getExtras());

        QRCodeDialog qrCodeDialog = new QRCodeDialog();
        qrCodeDialog.setArguments(args);
        qrCodeDialog.show(getSupportFragmentManager(), "qrCode dialog");
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.qrCodeRecyclerView);
        adapter = new qrCodeRecyclerViewAdapter(qrCodes, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().getExtras().get("username").toString().equals(currentUserHelper.getUsername())) {
            new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            QRCode qrCodeToDelete = qrCodes.get(viewHolder.getLayoutPosition());
            fireStoreController.removeUserFromQRCode(qrCodeToDelete)
                .addOnSuccessListener(v -> {
                    qrCodes.remove(viewHolder.getAbsoluteAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAbsoluteAdapterPosition());
//                    txtViewTotalScore.setText("Total Score: "+(totalScore - qrCodeToDelete.getWorth()));
//                    txtViewTotalCodes.setText("Total Codes Scanned: "+(Integer.parseInt(txtViewTotalCodes.getText().toString().split(":")[1]) - 1));
                })
                .addOnFailureListener(Throwable::printStackTrace);
        }
    };

    protected void fetchQRCodesOfUser(String username) {
       fireStoreController.getSpecifiedUsersCodes(username)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot existingQR : queryDocumentSnapshots) {
                    // Convert to qr code
                    QRCode qrCode = existingQR.toObject(QRCode.class);
                    qrCodes.add(qrCode);
                    totalScore += qrCode.getWorth();
                    if(qrCode.getAddress() == null)
                        qrCode.setAddress("No Location!");
                }

                qrCodes.sort((qrCode, t1) -> t1.getWorth() - qrCode.getWorth());

                initRecyclerView();
                txtViewTotalScore.setText("Total score: " + totalScore);
                txtViewTotalCodes.setText("Total number of codes scanned: " + qrCodes.size());
            });
    }

    @Override
    public void onQRclicked(String qrId) {
            Intent intent = new Intent(ViewProfileActivity.this , SingleQRActivity.class);
            intent.putExtra("codeID", qrId);
            startActivity(intent);
    }

}