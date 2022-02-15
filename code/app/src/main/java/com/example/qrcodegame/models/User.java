package com.example.qrcodegame.models;

import java.util.ArrayList;

public class User {

    private String username;
    private String email;
    private String phone;
    private int totalScore;
    private boolean isOwner;
    private ArrayList<String> collectedCodes = new ArrayList<>();
    private ArrayList<String> devices = new ArrayList<>();

    public User() {
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public ArrayList<String> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<String> deviceID) {
        this.devices = deviceID;
    }

    public ArrayList<String> getCollectedCodes() {
        return collectedCodes;
    }

    public void setCollectedCodes(ArrayList<String> collectedCodes) {
        this.collectedCodes = collectedCodes;
    }
}
