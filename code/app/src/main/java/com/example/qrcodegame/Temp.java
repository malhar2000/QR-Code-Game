package com.example.qrcodegame;

import android.util.Log;

public class Temp implements Comparable<Temp>{
    private String userName;
    private String score;
    private String num;


    public Temp(String userName, String score, String num) {
        this.userName = userName;
        this.score = score;
        this.num = num;
    }

    public Temp(String userName, String score) {
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
    public int compareTo(Temp temp) {
        return this.getScore().compareTo(temp.getScore());
    }
}
