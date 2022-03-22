package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.qrcodegame.adapters.OwnerViewPlayerActivityAdapter;
import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

// yet to be completed
// no issues
public class OwnerViewPlayerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FireStoreController fireStoreController = FireStoreController.getInstance();
    OwnerViewPlayerActivityAdapter player_adapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<User> listOfUsers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_view_player);

        listOfUsers = new ArrayList<>();
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fireStoreController.getAllPlayers().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                listOfUsers.add(d.toObject(User.class));
            }
            initRecycleView(listOfUsers);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.item_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.toolbar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Player Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                player_adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            String userName = listOfUsers.get(viewHolder.getAbsoluteAdapterPosition()).getUsername();
            OwnerViewPlayerActivityAdapter.listAll.remove(listOfUsers.get(viewHolder.getAbsoluteAdapterPosition()));
            listOfUsers.remove(viewHolder.getAbsoluteAdapterPosition());
            firestore.collection("Users").document(userName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    player_adapter.notifyItemRemoved(viewHolder.getAbsoluteAdapterPosition());
                 }
            });
            fireStoreController.getAllCodes().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        try {
                            firestore.collection("Codes").document(snapshot.getId()).update("players", FieldValue.arrayRemove(userName));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    };

    private void initRecycleView(ArrayList<User> userArrayList){
        recyclerView = findViewById(R.id.playerList);
        player_adapter = new OwnerViewPlayerActivityAdapter(this, userArrayList);
        recyclerView.setAdapter(player_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }

}