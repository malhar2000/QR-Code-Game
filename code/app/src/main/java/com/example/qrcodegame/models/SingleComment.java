package com.example.qrcodegame.models;


/**
 * This is a class that represents a QRCode comment.
 * Nested inner class.
 */
public class SingleComment {

    // attributes
    public String username;
    public String message;


    /**
     * SingleComment constructor
     */
    public SingleComment() {
    }

    // getters and setters

    /**
     * Returns a string rep of the username of the user who wrote the comment.
     * @return Returns a string rep of the username of the user who wrote the comment.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the person who wrote the comment to username.
     * @param username This is a string of the username of the User who is assigned the comment they made.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns a string rep of the message in the comment.
     * @return Returns a string rep of the message in the comment.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the comment message to the string message.
     * @param message This is a string of the message that is assigned.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a string rep that is properly formatted of the message in the comment.
     * @return Returns a string rep that is properly formatted of the message in the comment.
     */
    public String getCommentText() {
        return String.format("%s : %s", username , message );
    }
}
