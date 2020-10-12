/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tictactoe.server.game;

// The main game class
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToe implements Runnable{

    ArrayList<Player> players = new ArrayList<>();

    int[][] board = new int[3][3];
    
    public TicTacToe(Player p1, Player p2) {
        players.add(p1);
        players.add(p2);
    }
    
    @Override
    public void run() {
        init();
        alertPlayers("Start_Game");
        int numberOfMatches = players.get(0).getMatchesWithPlayer(players.get(1).name);
        alertPlayer("Match #" + (numberOfMatches + 1 )  + " against " + players.get(0).name, players.get(1));
        alertPlayer("Match #" + (numberOfMatches + 1 )  + " against " + players.get(1).name, players.get(0));
    }
    
    public void alertPlayer(String message, Player p) {
        try {
            new DataOutputStream(p.connection.getOutputStream()).writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void alertPlayers(String message) {
        for(Player p : players) {
            try {
                new DataOutputStream(p.connection.getOutputStream()).writeUTF(message);
            } catch (IOException ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void getInput() {
    }
    
    public void init() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                board[i][j] = -1;
            }
        }
    }
    
    public boolean tie() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == -1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean win(int player) {
        
        
        for(int c = 0; c < 3; c++) {
            // Columns
            if(board[0][c] == player && board[1][c] == player && board[2][c] == player) {
                return true;
            }
            
            // Rows
            if(board[c][0] == player && board[c][1] == player && board[c][2] == player) {
                return true;
            }
        }
        
        if(board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        
        if(board[0][2] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        } 
        
        return false;
        
    }
    
}
