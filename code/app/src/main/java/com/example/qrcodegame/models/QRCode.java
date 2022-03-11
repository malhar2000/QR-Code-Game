package com.example.qrcodegame.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This is a class that represents a QRCode object.
 */
public class QRCode {

    // attributes
    private String id;
    private int worth;
    private ArrayList<Double> coordinates = new ArrayList<>();
    private ArrayList<String> players = new ArrayList<>();
    private String imageUrl;
    private String address;

    /**
     * QRCode constructor
     */
    public QRCode() {
    }

    // getters and setters

    /**
     * Returns a string that is the address of the QR Code.
     * @return Returns a String that is an address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the QR Code address to address.
     * @param address This is a string of the address that is assigned.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns a string that is the URL of the QR Code image.
     * @return Returns a string of the URL of the QR Code image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the QR Code's imageUrl to imageUrl.
     * @param imageUrl This is a string of the imageUrl that is assigned.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns a string that is the id of the QR Code.
     * @return Returns a string that is the id of the QR Code.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the QR Code's id to id.
     * @param id This is a string of the id that is assigned.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns an int that is the worth of the QR Code.
     * @return Returns a int of the worth of the QR Code.
     */
    public int getWorth() {
        return worth;
    }

    /**
     * Sets the QR Code's worth to worth.
     * @param worth This is a int of the worth that is assigned.
     */
    public void setWorth(int worth) {
        this.worth = worth;
    }

    /**
     * Returns a 2 length double list that contains the coordinates of the QR Code.
     * @return Returns 2 length double list that has the coordinates of the QR Code.
     */
    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the QR Code's coordinates to coordinates.
     * @param coordinates This is a double array of the coordinates that are assigned.
     */
    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Returns a string list of all the players that have scanned that QR Code.
     * @return Returns a string list of all the players that have scanned that QR Code.
     */
    public ArrayList<String> getPlayers() {
        return players;
    }

    /**
     * Sets the QR Code's players to players.
     * @param players This is a string array of the players that are assigned.
     */
    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    /**
     * Returns a string properly formatted of the QR Code's attributes.
     * @return Returns a string properly formatted of the QR Code's attributes.
     */
    @NonNull
    @Override
    public String toString() {
        return "QRCode{" +
                "id='" + id + '\'' +
                ", worth=" + worth +
                ", coordinates=" + coordinates +
                ", players=" + players +
                ", city="+ address+
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}