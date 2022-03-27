package com.example.qrcodegame.controllers;

import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * controller class that aids in fetching data from the firestore
 * no issues
 */
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
    private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("Users");
    private final CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("Codes");
    private final CollectionReference commentsCollectionReference = FirebaseFirestore.getInstance().collection("Comments");

    public Task<QuerySnapshot> getAllCodesWithLocation() {
        return qrCollectionReference.whereNotEqualTo("coordinates", new ArrayList<>()).get();
    }

    public Task<QuerySnapshot> getAllCodes(){
        return qrCollectionReference.get();
    }

    public Task<QuerySnapshot> getAllPlayers() {
        return userCollectionReference.whereNotEqualTo("isOwner", true).get();
    }

    public Task<QuerySnapshot> getAllQRCodes() {
        return qrCollectionReference.orderBy("worth").get();
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

    /**
     * Saves a new code to the DB by doing the following:
     * 1. Stores the QR code
     * 2. Updates the player's profile whoever found it
     * @param codeToSave the QR code to save
     * @return A Task which is completed once both of these subtasks are done.
     */
    public Task<Void> saveNewQrCode(QRCode codeToSave){

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayUnion(codeToSave.getId()));
        updates.put("totalScore", FieldValue.increment(codeToSave.getWorth()));

        return Tasks.whenAll(
                qrCollectionReference.document(codeToSave.getId()).set(codeToSave),
                FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId()).update(updates)
        );
    }

    /**
     * Updates a code in the DB by doing the following:
     * 1. Adding player name to QR code
     * 2. Updates the player's profile whoever found it
     * @param codeToSave QRCode to update
     * @return A Task which is completed once both of these subtasks are done.
     */
    public Task<Void> updateExistingQrCode(QRCode codeToSave){
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayUnion(codeToSave.getId()));
        updates.put("totalScore", FieldValue.increment(codeToSave.getWorth()));
        return Tasks.whenAll(
                qrCollectionReference.document(codeToSave.getId()).update("players", FieldValue.arrayUnion(currentUserHelper.getUsername())),
                FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId()).update(updates)
        );
    }

    public Task<Void> deleteQRCode(QRCode qrCodeToDelete) {

        // All pending tasks
        ArrayList<Task<Void>> allTasks = new ArrayList<>();

        // Decreasing score and qrCode
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayRemove(qrCodeToDelete.getId()));
        updates.put("totalScore", FieldValue.increment( -1 * qrCodeToDelete.getWorth()));

        // Put all updates in one tasks
        qrCodeToDelete.getPlayers().forEach(playerUsername -> {
            Task<Void> updateTask =  userCollectionReference.document(playerUsername).update(updates);
            allTasks.add(updateTask);
        });
        // Delete qr code.
        allTasks.add(qrCollectionReference.document(qrCodeToDelete.getId()).delete());
        //
        return Tasks.whenAll(allTasks);
    }

    public Task<Void> removeUserFromQRCode(QRCode qrCode) {

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("collectedCodes", FieldValue.arrayRemove(qrCode.getId()));
        updates.put("totalScore", FieldValue.increment( -1 * qrCode.getWorth()));

        return Tasks.whenAll(
            userCollectionReference.document(currentUserHelper.getFirebaseId()).update(updates),
            qrCollectionReference.document(qrCode.getId()).update("players", FieldValue.arrayRemove(currentUserHelper.getUsername()))
        );
    }

}
