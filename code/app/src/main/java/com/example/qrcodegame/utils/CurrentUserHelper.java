package com.example.qrcodegame.utils;

import java.util.ArrayList;

public class CurrentUserHelper {

    private static CurrentUserHelper single_instance = null;

    public static CurrentUserHelper getInstance() {
        if (single_instance == null) {
            single_instance = new CurrentUserHelper();
        }
        return single_instance;
    }

    private String username;
    private String uniqueID;
    private boolean isOwner;
    private String firebaseId;
    private ArrayList<Double> currentLocation = new ArrayList<>();

    private CurrentUserHelper() {

    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public ArrayList<Double> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(ArrayList<Double> currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }
}
