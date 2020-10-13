package com.tictactoe.server.game;

import com.tictactoe.server.Database.GameResult;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Authored by: Bailey Costello
 * 2020
 */

public class Player implements Serializable {

    // The unique id of the player in the db
    public int id;

    // The name of the player
    public String name;

    // list of the different games the user has played
    public ArrayList<GameResult> gameHistory;
    
    
    public transient Socket connection;
    
    public double winPercentage() {
        
        double counter = 0;
        
        for(GameResult gr : gameHistory) {
            if(gr.won == id) {
                counter++;
            }
        }
        
        return counter / gameHistory.size();
        
    }
    
    public int getMatchesWithPlayer(String otherplayer) {
        int count = 0;
    
        for(GameResult gr : gameHistory) {
            if(gr.opponent.equals(otherplayer)) {
                count++;
            }
        }
        
        return count;
    }
    
}
