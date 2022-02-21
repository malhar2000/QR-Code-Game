package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTimeActivity extends AppCompatActivity {

    // objects used
    private Button btnGo;
    private EditText edtTxtUserName, edtTxtEmail, edtTxtPhoneNumber;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    ArrayList<String> allUsernames = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        getSupportActionBar().hide();

        btnGo = (Button) findViewById(R.id.buttonGo);
        edtTxtUserName = (EditText)  findViewById(R.id.editTextUsername);
        edtTxtPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        edtTxtEmail = (EditText) findViewById(R.id.editTextEmail);

        fetchAllUsernames();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredUsername = edtTxtUserName.getText().toString();
                String enteredEmail = edtTxtEmail.getText().toString();
                String enteredPhoneNumber = edtTxtPhoneNumber.getText().toString();


                boolean uniqueUsername = !allUsernames.contains(enteredUsername);

                if (enteredUsername.equals(""))
                {
                    Toast.makeText(FirstTimeActivity.this, "Username is blank", Toast.LENGTH_LONG).show();
                }

                else if (!uniqueUsername)
                {
                    Toast.makeText(FirstTimeActivity.this, "Username is already taken", Toast.LENGTH_LONG).show();
                }
                else
                {
                    User newUser = new User();
                    newUser.setUsername(enteredUsername);
                    newUser.getDevices().add(CurrentUserHelper.getInstance().getUniqueID());

                    if (!enteredEmail.equals("")) {
                        newUser.setEmail(enteredEmail);
                    }
                    if (!enteredPhoneNumber.equals("")) {
                        newUser.setPhone(enteredPhoneNumber);
                    }

                    CurrentUserHelper.getInstance().setUsername(enteredUsername);
                    CurrentUserHelper.getInstance().setOwner(false);

                    addUser(newUser);

                }
            }
        });
    }

    private void addUser(User newUserToAdd) {
        db.collection("Users")
                .add(newUserToAdd)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("added user success", "DocumentSnapshot written with ID: " + documentReference.getId());
                        CurrentUserHelper.getInstance().setFirebaseId(documentReference.getId());
                        CurrentUserHelper.getInstance().setPhone(newUserToAdd.getPhone());
                        CurrentUserHelper.getInstance().setEmail(newUserToAdd.getEmail());
                        CurrentUserHelper.getInstance().setUsername(newUserToAdd.getUsername());
                        Intent intent = new Intent(FirstTimeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("add user fail", "Error adding document", e);
                    }
                });
    }

    protected void fetchAllUsernames() {
        allUsernames.clear();
        db.collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot existingUser : queryDocumentSnapshots) {
                            allUsernames.add(existingUser.get("username").toString());
                        }
                    }
                });
    }
}
