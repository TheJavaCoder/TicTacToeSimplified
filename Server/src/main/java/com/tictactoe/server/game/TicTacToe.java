/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tictactoe.server.game;

// The main game class
import java.util.ArrayList;

public class TicTacToe implements Runnable{

    ArrayList<Player> players;

    int[][] board = new int[3][3];
    
    public TicTacToe(Player p1, Player p2) {
        players.add(p1);
        players.add(p2);
    }
    
    @Override
    public void run() {
        init();
        
        
        
    }
    
    public void getInput() {
    
        
        
    }
    
    public void init() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; i < 3; j++) {
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
