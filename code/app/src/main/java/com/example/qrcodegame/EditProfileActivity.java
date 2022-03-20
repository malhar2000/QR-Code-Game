package com.example.qrcodegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    EditText editUsername, editEmail, editPhone;
    DocumentReference userDocument; //, emailDocument, phoneDocument;
    CurrentUserHelper currentUserHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0F9D58")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initItems();
    }

    /**
     * Initialising Buttons and Edit Text items. Returns void, Arguments: None
     */
    private void initItems(){
        currentUserHelper = CurrentUserHelper.getInstance();
        userDocument = FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserHelper.getFirebaseId());
//        emailDocument = FirebaseFirestore.getInstance().collection("Users")
//                .document(currentUserHelper.getFirebaseId());
//        phoneDocument = FirebaseFirestore.getInstance().collection("Users")
//                .document(currentUserHelper.getFirebaseId());
//        linkAccount = findViewById(R.id.linkAccount);

        editEmail = findViewById(R.id.editEmail);
        editEmail.setText(currentUserHelper.getEmail());
        editPhone = findViewById(R.id.editPhone);
        editPhone.setText(currentUserHelper.getPhone());
        editUsername = findViewById(R.id.editUsername);
        editUsername.setText(currentUserHelper.getUsername());
        ImageButton img = findViewById(R.id.imageHomeButtonEdit);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
