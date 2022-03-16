package com.example.qrcodegame.models;

import java.util.ArrayList;

/**
 * This is a class that represents a QRCode's comments.
 */
public class Comments {
    // attributes
    private ArrayList<SingleComment> allComments;

    /**
     * Comments constructor
     */
    public Comments() {
        allComments = new ArrayList<>();
    }

    // getters and setters

    /**
     * Returns an array of SingleComments that a QR Code has.
     * @return Returns an array of SingleComments that a QR Code has.
     */
    public ArrayList<SingleComment> getAllComments() {
        return allComments;
    }

    /**
     * Sets allComments to an array of SingleComments that a QR Code has.
     * @param allComments This is an array of SingleComments that is assigned.
     */
    public void setAllComments(ArrayList<SingleComment> allComments) {
        this.allComments = allComments;
    }
}