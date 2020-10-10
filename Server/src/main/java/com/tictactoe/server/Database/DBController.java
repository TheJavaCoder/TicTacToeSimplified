package com.tictactoe.server.Database;

import com.tictactoe.server.game.Player;
import java.util.ArrayList;
import javafx.collections.ObservableList;

// To be a db controller you have to implement these functions
public interface DBController {

    public ArrayList<String> requiredTables = new ArrayList<String>() {
        {
            add("users");
            add("games");
        }
    };

    public ArrayList<String> requiredColumns = new ArrayList<String>() {
        {
            add("UserName TEXT(50)");
            add("PlayerOne Number, PlayerTwo Number, Win Number, Date Text");
        }
    };

    // Init of the db controller (setting up constants for each db type and creating tables if necessary)
    void init();
    
    // Adds a player to the user table
    void addPlayer(String name);

    // Searching for the player in the db by id
    String getPlayer(int id);
    
    // Searching for player in the db by name
    Player getPlayer(String name);

    // updating game status
    void updateGameStats(Player you, Player opponent, boolean won);

    ObservableList<ObservableList<String>> getGames();

}
