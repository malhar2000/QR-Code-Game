package com.example.qrcodegame.models;

import java.util.ArrayList;

/**
 * This is a class that represents a User object.
 */
public class User {

    // attributes
    private String username;
    private String email;
    private String phone;
    private int totalScore;
    private boolean isOwner;
    private ArrayList<String> collectedCodes = new ArrayList<>();
    private ArrayList<String> devices = new ArrayList<>();
    private int userRank;

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    /**
     * User constructor
     */
    public User() {
    }

    // getters and setters are paired together

    /**
     * Returns a boolean that is true if User is owner; false otherwise.
     * @return Returns a boolean.
     */
    public boolean getIsOwner() {
        return isOwner;
    }

    /**
     * Sets true if User is owner; false otherwise.
     * @param owner This is a boolean that is assigned.
     */
    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }

    /**
     * Returns a string rep of the user's username.
     * @return Returns a string that is a username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username to username.
     * @param username This is the username String that is assigned.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns a string rep of the user's email.
     * @return Returns a string that is an email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email to email.
     * @param email This is the email String that is assigned.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string rep of the user's phone number.
     * @return Returns a string that is a phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number to phone.
     * @param phone This is the phone number String that is assigned.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns an integer that is the user's total score.
     * @return Returns a integer that is the total score.
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Sets the user's total score to totalScore.
     * @param totalScore This is the total score integer that is assigned.
     */
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * Returns a string list of all the devices of the user.
     * @return Returns a string list of devices.
     */
    public ArrayList<String> getDevices() {
        return devices;
    }

    /**
     * Sets the user's devices they use to play the game.
     * @param deviceID This is the String array that contains the user's devices they use.
     */
    public void setDevices(ArrayList<String> deviceID) {
        this.devices = deviceID;
    }

    /**
     * Returns a string list of all the collected codes by the user.
     * @return Returns a string of the collected QR codes.
     */
    public ArrayList<String> getCollectedCodes() {
        return collectedCodes;
    }

    /**
     * Sets the user's qr codes they scanned.
     * @param collectedCodes This is the String array that contains the user's scanned QR codes.
     */
    public void setCollectedCodes(ArrayList<String> collectedCodes) {
        this.collectedCodes = collectedCodes;
    }
}