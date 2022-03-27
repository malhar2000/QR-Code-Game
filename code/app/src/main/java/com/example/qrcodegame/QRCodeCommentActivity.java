package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.qrcodegame.adapters.CommentRecycleViewAdapter;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// the activity where the users can see and add comments to a qr code scanned by themselves or others
// no issues
public class QRCodeCommentActivity extends AppCompatActivity {

    TextView setQRCodeWorth;
    EditText addComments;
    ImageButton homeButton;
    Button addButton;

    //gets detail about current User
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    Map<String, Object> comments = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CommentRecycleViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<String> getComments = new ArrayList<>();

    /**
     * Deafult onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_comment);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setQRCodeWorth = findViewById(R.id.textViewDisplayWorth);
        addComments = findViewById(R.id.add_comments);
        addButton = findViewById(R.id.addButtonComments);
        homeButton = findViewById(R.id.imageHomeButton);


        getData();

        getSupportActionBar().setTitle("Comments for: "+getIntent().getStringExtra("QRCodeCommentActivity"));
        setQRCodeWorth.setText("Worth: "+getIntent().getStringExtra("Worth"));


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    /**
     * This function adds User comment to the firestore with the UserName
     * @param view The onClick button for the Add
     */
    public void storeComments(View view){
        comments.put(currentUserHelper.getUsername(), FieldValue.arrayUnion(currentUserHelper.getUsername()+": "+addComments.getText().toString()));
        db.collection("Comments").document(getIntent().getStringExtra("QRCodeCommentActivity"))
                .set(comments, SetOptions.merge());
        getComments.add(currentUserHelper.getUsername()+": "+addComments.getText().toString());
        adapter.notifyDataSetChanged();
        addComments.setText("");
    }

    /**
     *Restores comment for the particular QRCode from the fireStore
     */
    public void getData(){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("Comments").document(getIntent().getStringExtra("QRCodeCommentActivity"));
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> map = new HashMap<>();
                DocumentSnapshot doc = task.getResult();
                map = doc.getData();
                if(map == null)
                {
                    initRecyclerView();
                    return;
                }
                for(Map.Entry<String, Object> e: map.entrySet()){
                    ArrayList<String> n = (ArrayList<String>) e.getValue();
                    getComments.addAll(n);
                }initRecyclerView();
            }
        });


    }

    /**
     *Initlizing the recycleView to start displaying content
     */
    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycle_view_comments);
        adapter = new CommentRecycleViewAdapter(getComments, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.smoothScrollToPosition(getComments.size() - 1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }

}