package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcodegame.controllers.FireStoreController;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class FirstTimeActivity extends AppCompatActivity {

    private EditText edtTxtUserName;
    private EditText edtTxtEmail;
    private EditText edtTxtPhoneNumber;
    private EditText edtTxtAdminPin;

    private final FireStoreController fireStoreController = FireStoreController.getInstance();
    private final CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    ArrayList<String> allUsernames = new ArrayList<>();

    private final Integer ADMIN_PIN = 9999;

    /**
     * Binds elements, as well as fetches al usernames from db
     *
     * @param savedInstanceState Android default.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // objects used
        Button btnGo = (Button) findViewById(R.id.buttonGo);
        edtTxtUserName = (EditText) findViewById(R.id.editTextUsername);
        edtTxtPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        edtTxtEmail = (EditText) findViewById(R.id.editTextEmail);
        edtTxtAdminPin = (EditText) findViewById(R.id.editTextAdminPin);

        fetchAllUsernames();

        btnGo.setOnClickListener(v -> submitDetails());
    }

    /**
     * Given a User object, this method saves it to the database. User must be checked for unique username before calling this method.
     * Afterwards, this method redirects to the correct next activity depending on admin status
     *
     * @param newUserToAdd user object
     */
    private void addUser(User newUserToAdd) {
        fireStoreController.addUser(newUserToAdd)
                .addOnSuccessListener(documentReference -> {

                    currentUserHelper.setFirebaseId(newUserToAdd.getUsername());
                    currentUserHelper.setPhone(newUserToAdd.getPhone());
                    currentUserHelper.setEmail(newUserToAdd.getEmail());
                    currentUserHelper.setUsername(newUserToAdd.getUsername());
                    currentUserHelper.setOwner(newUserToAdd.getIsOwner());

                    // Switch to the correct activity.
                    Intent intent;
                    if (newUserToAdd.getIsOwner()) {
                        // If owner, start the owner views
                        intent = new Intent(FirstTimeActivity.this, OwnerHomeActivity.class);
                    } else {
                        // Else start the regular user views
                        intent = new Intent(FirstTimeActivity.this, MainActivity.class);
                    }
                    // Switch
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.w("add user fail", "Error adding document", e));
    }

    /**
     * Fetches all the usernames from the DB and populates the username array.
     */
    protected void fetchAllUsernames() {
        allUsernames.clear();
        fireStoreController.getAllUsers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot existingUser : queryDocumentSnapshots) {
                        allUsernames.add(Objects.requireNonNull(existingUser.get("username")).toString());
                    }
                });
    }

    /**
     * Fetches all the info from the text fields and adds it into the database. If there are any problems, it will show a toast.
     */
    protected void submitDetails() {

        String enteredUsername = edtTxtUserName.getText().toString();
        String enteredEmail = edtTxtEmail.getText().toString();
        String enteredPhoneNumber = edtTxtPhoneNumber.getText().toString();

        Integer enteredAdminPin = 0000;
        try {
            enteredAdminPin = Integer.parseInt(edtTxtAdminPin.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean uniqueUsername = !allUsernames.contains(enteredUsername);

        if (enteredUsername.equals("")) {
            Toast.makeText(FirstTimeActivity.this, "Username is blank", Toast.LENGTH_LONG).show();
        } else if (!uniqueUsername) {
            Toast.makeText(FirstTimeActivity.this, "Username is already taken", Toast.LENGTH_LONG).show();
        } else {
            User newUser = new User();

            newUser.setUsername(enteredUsername);
            newUser.getDevices().add(CurrentUserHelper.getInstance().getUniqueID());
            newUser.setIsOwner(enteredAdminPin.equals(ADMIN_PIN));

            if (!enteredEmail.equals("")) {
                newUser.setEmail(enteredEmail);
            }
            if (!enteredPhoneNumber.equals("")) {
                newUser.setPhone(enteredPhoneNumber);
            }
            addUser(newUser);
        }
    }
}
