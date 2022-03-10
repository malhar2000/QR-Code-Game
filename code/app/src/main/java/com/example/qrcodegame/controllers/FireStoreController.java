package com.example.qrcodegame.controllers;

//import com.example.qrcodegame.models.Comment;
import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.models.SingleComment;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class FireStoreController {

    private static FireStoreController singleInstance = null;

    /**
     * Singleton constructor
     * @return returns the singular object
     */
    public static FireStoreController getInstance() {
        System.out.println("FirestoreID = " + CurrentUserHelper.getInstance().getFirebaseId());
        if (singleInstance == null) {
            singleInstance = new FireStoreController();
        }
        return singleInstance;
    }

    public FireStoreController() {

    }
    // Helper Var
    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    // Firestore Variables
    //private final
    private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("Users");
    private final CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("Codes");
    private final CollectionReference commentsCollectionReference = FirebaseFirestore.getInstance().collection("Comments");
    private final StorageReference imageLocationStorage = FirebaseStorage.getInstance().getReference().child("images");

    public Task<QuerySnapshot> getAllCodesWithLocation() {
        return qrCollectionReference.whereNotEqualTo("coordinates", new ArrayList<>()).get();
    };

    public Task<QuerySnapshot> getAllPlayers() {
        return userCollectionReference.whereNotEqualTo("isOwner", true).get();
    }

    public Task<QuerySnapshot> getAllUsers() {
        return userCollectionReference.get();
    }

    public Task<QuerySnapshot> getSpecifiedUsersCodes(String username) {
        return qrCollectionReference.whereArrayContains("players", username).get();
    }

    public Task<DocumentSnapshot> getSingleQRCode(String qrCodeId) {
        return qrCollectionReference.document(qrCodeId).get();
    }

    public DocumentReference setListenerForSingleQRCodeComments(String codeID) {
        return commentsCollectionReference.document(codeID);
    }

    public Task<DocumentSnapshot> getSingleQRCodeComments(String codeID) {
        return commentsCollectionReference.document(codeID).get();
    }

    public void storeCommentAtId(String codeID, SingleComment commentToStore) {
        commentsCollectionReference
                .document(codeID)
                .update("allComments", FieldValue.arrayUnion(commentToStore));
    }

    public Task<Void> switchProfile(String newUserNameToSwitchTo){
        Task<Void> removeFromCurrentUserProfile = userCollectionReference.document(currentUserHelper.getFirebaseId()).update("devices", FieldValue.arrayRemove(currentUserHelper.getUniqueID()));
        Task<Void> addToNewUserProfile = userCollectionReference.document(newUserNameToSwitchTo).update("devices", FieldValue.arrayUnion(currentUserHelper.getUniqueID()));
        return Tasks.whenAll(removeFromCurrentUserProfile, addToNewUserProfile);
    }

    public Task<Void> addUser(User newUserToAdd) {
        return userCollectionReference
                .document(newUserToAdd.getUsername())
                .set(newUserToAdd);
    }

    public Task<QuerySnapshot> findUserBasedOnDeviceId(){
        return userCollectionReference.whereArrayContains("devices", currentUserHelper.getUniqueID()).get();
    }

    public Task<QuerySnapshot> checkQRExists(String id) {
        return qrCollectionReference.whereEqualTo("id",id).get();
    }

    public Task<Void> saveNewQrCode(QRCode codeToSave){

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayUnion(codeToSave.getId()));
        updates.put("totalScore", FieldValue.increment(codeToSave.getWorth()));

        return Tasks.whenAll(
                qrCollectionReference.document(codeToSave.getId()).set(codeToSave),
                FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId()).update(updates)
        );
    }

    public Task<Void> updateExistingQrCode(QRCode codeToSave){
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayUnion(codeToSave.getId()));
        updates.put("totalScore", FieldValue.increment(codeToSave.getWorth()));
        return Tasks.whenAll(
                qrCollectionReference.document(codeToSave.getId()).update("players", FieldValue.arrayUnion(currentUserHelper.getUsername())),
                FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId()).update(updates)
        );
    }

}
