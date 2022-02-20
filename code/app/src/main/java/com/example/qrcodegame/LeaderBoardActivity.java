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

import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LeaderBoardActivity extends AppCompatActivity implements Comparable<String>{

    RecycleViewAdapter adapter;
    RecyclerView recyclerView;

    TextView myScoreByRank;
    TextView myScoreByCode;
    Button backBtn;

    EditText searchPlayer;
    ArrayList<String> forScannedCode = new ArrayList<>();



    private final List<SaveLeaderInfo> saveLeaderInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        getContentFromFireBase();
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void filter(String text){
        ArrayList<SaveLeaderInfo> fList = new ArrayList<>();
        for(SaveLeaderInfo t : saveLeaderInfos){
            if(t.getUserName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                fList.add(t);
            }
        }
        adapter.filterList(fList);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycle_view);
        adapter = new RecycleViewAdapter(saveLeaderInfos, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getContentFromFireBase(){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int size;
                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Map<String, Object> map = snapshot.getData();
                    assert map != null;
                    size = map.get("collectedCodes").toString().split(",").length;

                    for(String key: map.keySet()){
                        if(key.equals("username")) {
                            forScannedCode.add(size+","+map.get(key).toString());
                            saveLeaderInfos.add(new SaveLeaderInfo(map.get(key).toString(),
                                    map.get("totalScore").toString()));
                        }
                    }
                }
                int count = 1;
                Collections.sort(saveLeaderInfos, Collections.reverseOrder());
                Collections.sort(forScannedCode, Collections.reverseOrder());

                //This is for Rank by Code
                for(String s : forScannedCode) {
                    if (s.split(",")[1].contains(CurrentUserHelper.getInstance().getUsername())){
                        myScoreByCode.setText(String.valueOf(count));
                        break;
                    }
                    count++;
                }

                //This is for the Rank by Score
                count = 1;
                for(SaveLeaderInfo t : saveLeaderInfos){
                    t.setNum(count+"");
                    if(t.getUserName().equals(CurrentUserHelper.getInstance().getUsername())) {
                        myScoreByRank.setText(t.getNum());
                    }
                    count++;
                }
                initRecyclerView();
            }
        });

    }

    @Override
    public int compareTo(String s) {
        String arr[] = s.split(",");
        return Integer.parseInt(arr[0]);
    }
}