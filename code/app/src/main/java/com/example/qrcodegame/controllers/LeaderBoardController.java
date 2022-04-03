package com.example.qrcodegame.controllers;

import com.example.qrcodegame.models.User;
import com.example.qrcodegame.utils.CurrentUserHelper;

import java.util.ArrayList;

/**
 * Helper functions to assist Leaderboard views.
 * No issues
 */
public class LeaderBoardController {

    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();


    /**
     * This function sorts all Users by number of QRCode scanned
     * @param allPlayers allPlayers arraylist contenting details about User object.
     * @return returns the int which is the rank of current user by number of QRCode scanned
     */
    public int getScoreByNumberOfQrScanned(ArrayList<User> allPlayers) {
        // Calculating Score based on # of qr scanned
        //sorting arraylist in reverse order
        allPlayers.sort((user, t1) -> t1.getCollectedCodes().size() - user.getCollectedCodes().size());
        for (int i = 0; i < allPlayers.size(); i++) {
            if (allPlayers.get(i).getUsername().equals(currentUserHelper.getUsername())) {
                return i + 1;
            }
        }
        return -1;
    };

    /**
     * This function sorts all Users by scores
     * @param allPlayers arraylist contenting details about User object.
     * @return returns the int which is the rank of current user by score
     */
    public int getScoreByRank(ArrayList<User> allPlayers) {
        // Calculating Score based on Rank
        //sorting arraylist in reverse order
        allPlayers.sort((user, t1) -> t1.getTotalScore() - user.getTotalScore());
        for(int i = 0; i < allPlayers.size(); i++){
            allPlayers.get(i).setUserRank(i+1);
        }
        for (int i = 0; i < allPlayers.size(); i++) {
            if (allPlayers.get(i).getUsername().equals(currentUserHelper.getUsername())) {
                return i + 1;
            }
        }
        return -1;
    }

}
