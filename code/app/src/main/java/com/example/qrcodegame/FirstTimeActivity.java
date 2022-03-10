package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTimeActivity extends AppCompatActivity {

    // objects used
    private Button btnGo; //, scanQRCodeNewUserBtn;
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
//        scanQRcodeNewUserBtn = (Button) findViewById(R.id.scanQRBtnNewUser);

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

//        scanQRcodeNewUserBtn.setOnClickListener(view -> {
//            IntentIntegrator integrator = new IntentIntegrator(this);
//            integrator.setCaptureActivity(CaptureAct.class);
//            integrator.setOrientationLocked(true);
//            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//            integrator.setPrompt("New User Scanning Code");
//            integrator.initiateScan();
//        });
    }

//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() != null) {
//                String resultString = result.getContents();
//                if (resultString.startsWith("QRCODEGAME_Username=")) {
//                    // Get username
//                    String username = resultString.split("=")[1];
//                    // Updates
//                    HashMap<String, Object> updates = new HashMap<>();
//                    updates.put("devices", FieldValue.arrayUnion(CurrentUserHelper.getInstance().getUniqueID()));
//                    db.collection("Users")
//                            .document(username)
//                            .update(updates)
//                            .addOnSuccessListener(v -> {
//                               Intent intent = new Intent(this, SplashScreenActivity.class);
//                               startActivity(intent);
//                               finish();
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
//                            });
//
//                } else {
//                    Toast.makeText(this, "Invalid QR Code!", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(this, "Scanning cancelled!", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    /**
     * This adds the new user's username to the firebase database
     *
     * @param newUserToAdd This is the String representation of the new username to add
     */
    private void addUser(User newUserToAdd) {
        db.collection("Users")
                .add(newUserToAdd)
                .addOnSuccessListener(documentReference -> {
                    // add the accompanying fields
                    CurrentUserHelper.getInstance().setFirebaseId(documentReference.getId());
                    CurrentUserHelper.getInstance().setPhone(newUserToAdd.getPhone());
                    CurrentUserHelper.getInstance().setEmail(newUserToAdd.getEmail());
                    CurrentUserHelper.getInstance().setUsername(newUserToAdd.getUsername());
                    // go to MainActivity
                    Intent intent = new Intent(FirstTimeActivity.this, MainActivity.class);
                    startActivity(intent);
                    // finish this activity
                    finish();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("add user fail", "Error adding document", e);
                    }
                });
    }

    /**
     * Fetches all the usernames from firestore to user to check for duplicates
     */
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
