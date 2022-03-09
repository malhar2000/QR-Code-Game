package com.example.qrcodegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    Button backBtn;
    EditText editUsername, editEmail, editPhone;
    DocumentReference userDocument; //, emailDocument, phoneDocument;
    CurrentUserHelper currentUserHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init_items();
    }

    /**
     * Initialising Buttons and Edit Text items. Returns void, Arguments: None
     */
    private void init_items(){
        currentUserHelper = CurrentUserHelper.getInstance();
        userDocument = FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserHelper.getFirebaseId());
//        emailDocument = FirebaseFirestore.getInstance().collection("Users")
//                .document(currentUserHelper.getFirebaseId());
//        phoneDocument = FirebaseFirestore.getInstance().collection("Users")
//                .document(currentUserHelper.getFirebaseId());
//        linkAccount = findViewById(R.id.linkAccount);
        backBtn = findViewById(R.id.backBtn);
        editEmail = findViewById(R.id.editEmail);
        editEmail.setText(currentUserHelper.getEmail());
        editPhone = findViewById(R.id.editPhone);
        editPhone.setText(currentUserHelper.getPhone());
        editUsername = findViewById(R.id.editUsername);
        editUsername.setText(currentUserHelper.getUsername());
    }

    /**
     * OnClick function for back Button
     * Returns : None
     * @param view Takes Back Button View as input
     */
    public void backBtnOnClick(View view){
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    /**
     * OnClick function for Add to QR Code Button
     * Returns : None
     * @param view Takes Link Button View as input
     */
    public void saveAccountOnClick(View view){
        HashMap<String, Object> updates = new HashMap<>();
        String username = editUsername.getText().toString();
        String email = editEmail.getText().toString();
        String phone = editPhone.getText().toString();
        if (username.equals("")){
            Toast.makeText(EditProfileActivity.this,
                    "Username is blank", Toast.LENGTH_LONG).show();
            return;
        }

        if(email.equals("")) {
            Toast.makeText(EditProfileActivity.this,
                    "Email Address is blank", Toast.LENGTH_LONG).show();
            return;
        }
        updates.put("username", username);
        updates.put("email", email);
        updates.put("phone", phone);
        userDocument.update(updates);
        CurrentUserHelper.getInstance().setPhone(phone);
        CurrentUserHelper.getInstance().setEmail(email);
        CurrentUserHelper.getInstance().setUsername(username);
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}