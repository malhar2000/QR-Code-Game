package com.example.qrcodegame.models;

import java.util.ArrayList;

public class Comments {

    private ArrayList<SingleComment> allComments;

    public Comments() {
        allComments = new ArrayList<>();
    }

    public ArrayList<SingleComment> getAllComments() {
        return allComments;
    }

    public void setAllComments(ArrayList<SingleComment> allComments) {
        this.allComments = allComments;
    }

}
