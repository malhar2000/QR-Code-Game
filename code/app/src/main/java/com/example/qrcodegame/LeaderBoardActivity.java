package com.example.qrcodegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LeaderBoardActivity extends AppCompatActivity{

    RecycleViewAdapter adapter;
    RecyclerView recyclerView;

    TextView myScoreByRank;
    TextView myScoreByCode;

    EditText searchPlayer;

    private User user;
    String device;


    private final List<Temp> temps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        getContentFromFireBase();
        user = new User();
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
    }

    private void filter(String text){
        ArrayList<Temp> fList = new ArrayList<>();
        for(Temp t : temps){
            if(t.getUserName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                fList.add(t);
            }
        }
        adapter.filterList(fList);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycle_view);
        adapter = new RecycleViewAdapter(temps, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getContentFromFireBase(){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Map<String, Object> map = snapshot.getData();
                    assert map != null;
                    for(String key: map.keySet()){
                        if(key.equals("username")) {
                            temps.add(new Temp(map.get(key).toString(),
                                    map.get("totalScore").toString()));
                        }
                    }
                }
                int count = 1;
                Collections.sort(temps, Collections.reverseOrder());
                for(Temp t : temps){
                    t.setNum(count+"");
                    count++;
                }
                initRecyclerView();
            }
        });

    }

}