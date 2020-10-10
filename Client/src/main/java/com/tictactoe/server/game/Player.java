package com.tictactoe.server.game;

import com.tictactoe.server.Database.GameResult;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

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
            if(gr.won) {
                counter++;
            }
        }
        
        return counter / gameHistory.size();
        
    }

}
