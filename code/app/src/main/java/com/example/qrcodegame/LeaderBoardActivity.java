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

import com.example.qrcodegame.adapters.LeaderBoardAdapter;
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

public class LeaderBoardActivity extends AppCompatActivity{

    LeaderBoardAdapter adapter;
    RecyclerView recyclerView;

    TextView myScoreByRank;
    TextView myScoreByCode;
    Button backBtn;
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

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
        backBtn.setOnClickListener(view -> finish());

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
        adapter = new LeaderBoardAdapter(saveLeaderInfos, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getContentFromFireBase(){
        FirebaseFirestore.getInstance()
            .collection("Users")
            .addSnapshotListener((value, error) -> {

                forScannedCode.clear();
                saveLeaderInfos.clear();

                int size;
                assert value != null;
                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Map<String, Object> map = snapshot.getData();
                    assert map != null;
                    ArrayList<String> arr = (ArrayList<String>) map.get("collectedCodes");
                    assert arr != null;
                    size = arr.size();
                    arr.clear();
                    for(String key: map.keySet()){
                        if(key.equals("username")) {
                            forScannedCode.add(size+","+map.get(key).toString());
                            saveLeaderInfos.add(new SaveLeaderInfo(map.get(key).toString(),
                                    map.get("totalScore").toString()));
                        }
                    }
                }

                Collections.sort(saveLeaderInfos, Collections.reverseOrder());
                Collections.sort(forScannedCode, new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        String[] arr = s.split(",");
                        String[] arr1 = t1.split(",");
                        return Integer.parseInt(arr1[0]) - Integer.parseInt(arr[0]);
                    }
                });
                int count = 1;
                //This is for Rank by Code
                for(String s : forScannedCode) {
                    if (s.split(",")[1].equals(currentUserHelper.getUsername())){
                        myScoreByCode.setText(String.valueOf(count));
                        break;
                    }
                    count++;
                }

                //This is for the Rank by Score
                count = 1;
                for(SaveLeaderInfo t : saveLeaderInfos){
                    t.setNum(count+"");
                    if(t.getUserName().equals(currentUserHelper.getUsername())) {
                        myScoreByRank.setText(t.getNum());
                    }
                    count++;
                }
                initRecyclerView();
            });

    }

}