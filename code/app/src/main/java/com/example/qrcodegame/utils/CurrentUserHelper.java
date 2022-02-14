package com.example.qrcodegame.utils;

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

    private CurrentUserHelper() {

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
