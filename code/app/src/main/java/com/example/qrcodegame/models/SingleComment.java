package com.example.qrcodegame.models;


// Nested Inner Class
public class SingleComment {
    public String username;
    public String message;

    public SingleComment() {
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
