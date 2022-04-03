package com.example.qrcodegame.utils;

import android.Manifest;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

/**
 * Helper class to manage permission status
 */
public class AwaitingPermissionsHelper {

    private int userFoundInDB;
    private boolean locationGranted;
    private boolean cameraGranted;
    private AllVariablesTrueListener listener;


    /**
     * Constructor
     * @param listener object which responds once all permissions granted
     */
    public AwaitingPermissionsHelper(AllVariablesTrueListener listener) {
        userFoundInDB = 0; // 1 = user in db    2 = new user
        locationGranted = false;
        cameraGranted = false;
        this.listener = listener;
    }

    public interface AllVariablesTrueListener{
        void onAllVariablesTrue();
    }

    public int isUserFoundInDB() {
        return userFoundInDB;
    }

    public void setUserFoundInDB(int userFoundInDB) {
        this.userFoundInDB = userFoundInDB;
        checkUpdates();
    }

    public boolean isLocationGranted() {
        return locationGranted;
    }

    public void setLocationGranted(boolean locationGranted) {
        this.locationGranted = locationGranted;
        checkUpdates();
    }

    public boolean isCameraGranted() {
        return cameraGranted;
    }

    public void setCameraGranted(boolean cameraGranted) {
        this.cameraGranted = cameraGranted;
        checkUpdates();
    }

    private void checkUpdates(){
        if ((userFoundInDB != 0 ) && locationGranted && cameraGranted) {
            listener.onAllVariablesTrue();
        }
    }
}
