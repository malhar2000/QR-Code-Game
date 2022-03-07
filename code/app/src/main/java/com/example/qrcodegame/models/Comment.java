package com.example.qrcodegame.models;

public class Comment {

    private String username;
    private String message;

    public Comment() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommentText() {
        return String.format("%s : %s", username , message );
    }
}
