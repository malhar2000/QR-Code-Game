package com.example.qrcodegame;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodegame.adapters.LeaderBoardAdapter;
import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.controllers.LeaderBoardController;
import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * This is the leaderboard card view where users can see each others and their ranking
 * no issues
 */
public class LeaderBoardActivity extends AppCompatActivity{

    // View elements
    LeaderBoardAdapter adapter;
    RecyclerView recyclerView;
    TextView myScoreByRank;
    TextView myScoreByCode;
    TextView myScoreByUniqueNess;
    EditText searchPlayer;

    // Other things we need
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    private final FireStoreController fireStoreController = FireStoreController.getInstance();
    private final LeaderBoardController leaderBoardController = new LeaderBoardController();

    // Storing Data for activity
    private ArrayList<User> allPlayers = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param savedInstanceState this is for saving state and data on the display
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setup();

        myScoreByCode = findViewById(R.id.editTextRankByScanned);
        myScoreByRank = findViewById(R.id.editTextRankByScore);
        myScoreByUniqueNess = findViewById(R.id.rankByUniqueQRCode);

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

    }

    /**
     * This function sets the adapter to the search perfernce of the user
     * @param text User search input inside the search section is returned here
     */
    private void filter(String text){
        ArrayList<User> fList = new ArrayList<>();
        for(User u : allPlayers){
            if(u.getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                fList.add(u);
            }
        }
        adapter.filterList(fList);
    }

    /**
     * Initializing recycle display
     */
    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycle_view);
        adapter = new LeaderBoardAdapter(allPlayers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * We restore the data from the firestore by getting allplayers and turning into object User
     * and then sorting by worth and size of collectionCodes.
     * Very Expensive and time consuming.
     */
    public void setup(){

        allPlayers.clear();
        fireStoreController.getAllPlayers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Adding users to list
                    queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> allPlayers.add(documentSnapshot.toObject(User.class)));

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

        // Used for getting the unique QR code rank
        fireStoreController.getAllQRCodes().addOnSuccessListener(queryDocumentSnapshots -> {
            int rank = 1;
            for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                QRCode qrcode = d.toObject(QRCode.class);
                assert qrcode != null;
                if (qrcode.getPlayers().contains(currentUserHelper.getUsername())) {
                    break;
                } else {
                    rank++;
                }
            }
            myScoreByUniqueNess.setText("Your Rank By Unique Code: " + rank);
        });


    }

}