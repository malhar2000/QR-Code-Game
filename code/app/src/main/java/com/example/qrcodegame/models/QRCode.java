package com.example.qrcodegame.models;

import java.util.ArrayList;

public class QRCode {

    private String id;
    private int worth;
    private ArrayList<Double> coordinates = new ArrayList<>();
    private ArrayList<String> players = new ArrayList<>();
    private String imageUrl;

    public QRCode() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
    }

    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "id='" + id + '\'' +
                ", worth=" + worth +
                ", coordinates=" + coordinates +
                ", players=" + players +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
