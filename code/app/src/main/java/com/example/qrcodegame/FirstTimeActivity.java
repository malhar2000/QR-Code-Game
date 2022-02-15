package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirstTimeActivity extends AppCompatActivity {

    // objects used

    private Button btnGo;
    private EditText edtTxtUserName, edtTxtEmail, edtTxtPhoneNumber;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected boolean uniqueUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        btnGo = (Button) findViewById(R.id.buttonGo);
        edtTxtUserName = (EditText) findViewById(R.id.editTextUsername);
        edtTxtPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        edtTxtEmail = (EditText) findViewById(R.id.editTextEmail);


        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkIfUsernameUnique(edtTxtUserName.getText().toString());


                if (!uniqueUsername)
                {
                    Toast.makeText(FirstTimeActivity.this, "username is already taken", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.d("bruh", "bruh");
                }

            }
        });
    }

    protected void checkIfUsernameUnique(String enteredUsername) {

        db.collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        uniqueUsername = true;
                        for (DocumentSnapshot existingUser : queryDocumentSnapshots) {

                            // Log.d("added", existingUser.get("username").toString());
                            if (enteredUsername.equals(existingUser.get("username").toString()))
                            {
                                uniqueUsername = false;
                                break;
                            }
                        }
                    }

                });
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            uniqueUsername = true;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                if (enteredUsername.equals(document.getData().get("username").toString()))
//                                {
//                                    uniqueUsername = false;
//                                    Log.d("got users", document.getId() + " => " + document.getData().get("username"));
//                                    break;
//                                }
//                            }
//                        } else {
//                            Log.d("not got users", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }
}
