package com.example.qrcodegame;

public class SaveLeaderInfo implements Comparable<SaveLeaderInfo> {
    private final String userName;
    private final String score;
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
        return Integer.parseInt(this.getScore())-Integer.parseInt(saveLeaderInfo.getScore());
    }

}
