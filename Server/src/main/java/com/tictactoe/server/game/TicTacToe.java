/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tictactoe.server.game;

// The main game class
import com.tictactoe.server.App;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * Authored by: Bailey Costello 2020
 */
public class TicTacToe implements Runnable {

    ArrayList<Player> players = new ArrayList<>();

    UUID gameUUID;

    int[][] board = new int[3][3];

    public TicTacToe(Player p1, Player p2) {
        gameUUID = UUID.randomUUID();
        players.add(p1);
        players.add(p2);
    }

    @Override
    public void run() {
        init();
        alertPlayers("Start_Game");
        int numberOfMatches = players.get(0).getMatchesWithPlayer(players.get(1).name);
        alertPlayer("Match #" + (numberOfMatches + 1) + " against " + players.get(0).name, players.get(1));
        alertPlayer("Match #" + (numberOfMatches + 1) + " against " + players.get(1).name, players.get(0));

        int xPlayer = assignXO();

        for (int i = 0; i < board.length * board.length; i++) {

            Player p;

            if (i % 2 == 0) {
                p = players.get(xPlayer);
            } else {
                p = getOtherPlayer(players.get(xPlayer));
            }

            Move m = getInput(p);

            board[m.tileX][m.tileY] = (p.assigned == "X" ? 1 : 0);

            if (tie()) {
                Platform.runLater(() -> {

                    App.debug.appendText("\n-------------------------------------\nGame: " + gameUUID.toString() + " tied!" + "\n-------------------------------------\n");

                });
                
                alertPlayer("ENDSCREEN", getOtherPlayer(p));
                alertPlayer("Sorry, " + p.name + " is the winner.\n Would you like to play again?", getOtherPlayer(p));
                
                alertPlayer("ENDSCREEN", p);
                alertPlayer("You won!\n Would you like to play again?", p);
                
                App.getDB().updateGameStats(p, getOtherPlayer(p), -1, gameUUID.toString());
            }

            if (win(p)) {

                Platform.runLater(() -> {

                    App.debug.appendText("\n-------------------------------------\nGame: " + gameUUID.toString() + " is over!\nWinner: " + p.name + "\n-------------------------------------\n");

                });
                
                alertPlayer("ENDSCREEN", getOtherPlayer(p));
                alertPlayer("Sorry, " + p.name + " is the winner. \n Would you like to play again?", getOtherPlayer(p));
                
                alertPlayer("ENDSCREEN", p);
                alertPlayer("You won!\n Would you like to play again?", p);
                
                App.getDB().updateGameStats(p, getOtherPlayer(p), p.id, gameUUID.toString());
            }

            alertPlayer("VALID_MOVE", p);
            alertPlayer("PLAYER_MOVE", getOtherPlayer(p));
            sendMove(m, getOtherPlayer(p));

            alertPlayer("YOUR_TURN", getOtherPlayer(p));

        }
    }

    public int assignXO() {

        int player = new Random().nextInt(2);

        int otherPlayer = 0;
        for (int i = 0; i < players.size(); i++) {
            if (i != player) {
                otherPlayer = i;
                break;
            }
        }

        players.get(player).assigned = "X";
        players.get(otherPlayer).assigned = "O";

        alertPlayer("X", players.get(player));
        alertPlayer("O", players.get(otherPlayer));

        return player;
    }

    public Player getOtherPlayer(Player p) {

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).assigned != p.assigned) {
                return players.get(i);
            }
        }
        return null;
    }

    public void sendMove(Move m, Player p) {
        try {
            new ObjectOutputStream(p.connection.getOutputStream()).writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void alertPlayer(String message, Player p) {
        try {
            new DataOutputStream(p.connection.getOutputStream()).writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void alertPlayers(String message) {
        for (Player p : players) {
            try {
                new DataOutputStream(p.connection.getOutputStream()).writeUTF(message);
            } catch (IOException ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Move getInput(Player p) {

        boolean valid = false;

        Move m = null;
        while (!valid) {
            try {
                m = (Move) new ObjectInputStream(p.connection.getInputStream()).readObject();

                valid = isValid(m);

            } catch (Exception ex) {
                Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return m;
    }

    public void init() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = -1;
            }
        }
    }

    public boolean isValid(Move m) {

        return board[m.tileX][m.tileY] == -1;
    }

    public boolean tie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean win(Player p) {

        int player = (p.assigned == "X" ? 1 : 0);

        for (int c = 0; c < 3; c++) {
            // Columns
            if (board[0][c] == player && board[1][c] == player && board[2][c] == player) {
                return true;
            }

            // Rows
            if (board[c][0] == player && board[c][1] == player && board[c][2] == player) {
                return true;
            }
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }

        if (board[0][2] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }

        return false;

    }

}
