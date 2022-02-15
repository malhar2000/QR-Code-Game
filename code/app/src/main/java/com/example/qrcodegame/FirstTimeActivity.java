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


        btnGo = (Button) findViewById(R.id.buttonGo);
        edtTxtUserName = (EditText)  findViewById(R.id.editTextUsername);
        edtTxtPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        edtTxtEmail = (EditText) findViewById(R.id.editTextEmail);

       checkIfUsernameUnique();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredUsername = edtTxtUserName.getText().toString();
                String enteredEmail = edtTxtEmail.getText().toString();
                String enteredPhoneNumber = edtTxtPhoneNumber.getText().toString();


                boolean uniqueUsername = true;
                for (int i = 0; i < allUsernames.size(); i++)
                {
                    Log.d("all", allUsernames.get(i));
                    if (enteredUsername.equals(allUsernames.get(i)))
                    {
                        uniqueUsername = false;
                        break;
                    }
                }

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
                    if (!enteredEmail.equals("")) {
                        newUser.setEmail(enteredEmail);
                    }
                    if (!enteredPhoneNumber.equals("")) {
                        newUser.setEmail(enteredEmail);
                    }
                    addUser(newUser);

                    Intent intent = new Intent(FirstTimeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void addUser(User newUserToAdd) {
        // Add a new document with a generated id.
        Map<String, Object> data = new HashMap<>();
        data.put("name", newUserToAdd);

        db.collection("Users")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("added user success", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("add user fail", "Error adding document", e);
                    }
                });
    }

    protected void checkIfUsernameUnique() {
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
