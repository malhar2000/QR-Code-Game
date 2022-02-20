package com.example.qrcodegame;

public class SaveLeaderInfo implements Comparable<SaveLeaderInfo> {
    private String userName;
    private String score;
    private String num;

    public SaveLeaderInfo(String userName, String score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public String getScore() {
        return score;
    }

    public String getNum() {
        return num;
    }


    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public int compareTo(SaveLeaderInfo saveLeaderInfo) {
        return this.getScore().compareTo(saveLeaderInfo.getScore());
    }

}
