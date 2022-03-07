package com.example.qrcodegame.controllers;

import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;

import java.util.ArrayList;

public class LeaderBoardController {

    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

    public int getScoreByNumberOfQrScanned(ArrayList<User> allPlayers) {
        // Calculating Score based on # of qr scanned
        allPlayers.sort((user, t1) -> t1.getCollectedCodes().size() - user.getCollectedCodes().size());
        for (int i = 0; i < allPlayers.size(); i++) {
            if (allPlayers.get(i).getUsername().equals(currentUserHelper.getUsername())) {
                return i + 1;
            }
        }
        return -1;
    };

    public int getScoreByRank(ArrayList<User> allPlayers) {
        // Calculating Score based on Rank
        allPlayers.sort((user, t1) -> t1.getTotalScore() - user.getTotalScore());
        for (int i = 0; i < allPlayers.size(); i++) {
            if (allPlayers.get(i).getUsername().equals(currentUserHelper.getUsername())) {
                return i + 1;
            }
        }
        return -1;
    }

}
