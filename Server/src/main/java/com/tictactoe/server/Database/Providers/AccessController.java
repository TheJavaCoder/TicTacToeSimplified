package com.tictactoe.server.Database.Providers;

import com.tictactoe.server.Database.DBController;
import com.tictactoe.server.Database.GameResult;
import com.tictactoe.server.game.Player;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Authored by: Bailey Costello
 * 2020
 */

public class AccessController implements DBController {

    String dataFilePath = "C:/Data/Test/";
    String dataFile = "TicTacToe.accdb";

    private Connection conn;

    // Fully Tested
    @Override
    public void init() {
        try {
            // create the directory if it doesn't exist.
            new File(dataFilePath).mkdirs();

            // Attempt to load the driver...
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("Loaded Access Driver!");

            // Attempting to connect to the DB
            conn = DriverManager.getConnection("jdbc:ucanaccess://" + dataFilePath + dataFile + ";newdatabaseversion=V2010");
            System.out.println("Access Database connected!");

            // Building the statement object
            // Get meta data
            DatabaseMetaData dbMeta = conn.getMetaData();

            // For loop through the required tables
            for (int i = 0; i < requiredTables.size(); i++) {

                // Getting the table name
                String t = requiredTables.get(i);

                // Checking if the table exists
                ResultSet results = dbMeta.getTables(null, null, t, new String[]{"TABLE"});

                // Whoops it exists
                if (results.next()) {
                    System.out.println(t + " table already exists... Skipping");
                } else {
                    // Need to create the table 
                    String queryBuilder = "CREATE TABLE " + t + " (ID COUNTER PRIMARY KEY, ";

                    // Need to grab the column requirements.
                    queryBuilder = queryBuilder + requiredColumns.get(i) + ")";

                    conn.createStatement().executeUpdate(queryBuilder);

                    System.out.println("Created Table: [" + t + "]");
                }
            }

        } catch (Exception e) {
            System.out.println(" Make sure the ucanaccess.jdbcd driver is configured in your project... ");
        }
    }

    // Fully tested
    // Adds a player to the user table
    @Override
    public void addPlayer(String name) {

        name = name.trim();

        // Check if the player already exists
        if (getPlayer(name) == null) {
            try {
                String q = "INSERT INTO users (UserName) VALUES (?)";
                PreparedStatement pst = conn.prepareStatement(q);
                pst.setString(1, name);
                pst.execute();

                System.out.println("Added user: " + name);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("User: " + name + " already exists... Skipping ");
        }
    }

    // Fully Tested
    public String getPlayer(int id) {

        try {

            String query = "SELECT UserName FROM users WHERE ID =" + id;
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (Exception e) {
        }

        return null;
    }

    // Fully Tested
    // Returns a player object with their game history
    @Override
    public Player getPlayer(String name) {

        Player player = new Player();

        try {

            // Query the player's table with their name
            String query = "SELECT ID FROM users WHERE UserName = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ResultSet results = ps.executeQuery();

            // Build the begining of the player object
            if (results.next()) {
                player.id = results.getInt(1);
                player.name = name;

            } else {
                return null;
            }

            //query = "SELECT PlayerTwo, Win, Date FROM games WHERE PlayerOne = ? OR PlayerTwo = ?";
            ps = conn.prepareStatement("SELECT  iif(games.PlayerOne = ?,  (SELECT username FROM users WHERE id = games.PlayerTwo), (SELECT username FROM users WHERE id = games.PlayerOne)) AS oppenent, Win, Date FROM games WHERE PlayerOne = ? OR PlayerTwo = ?");
//            ps.setString(1, name);
//            ps.setString(2, "%" + name + "%");
            
            //ps = conn.prepareStatement(query);
            ps.setInt(1, player.id);
            ps.setInt(2, player.id);
            ps.setInt(3, player.id);
            results = ps.executeQuery();

            player.gameHistory = new ArrayList<>();

            // Loop through all the games
            while (results.next()) {

                // Build the object
                GameResult gr = new GameResult();

                // The other player
                gr.opponent = results.getString(1);

                // Boolean of the win
                gr.won = results.getInt(2);
                
                // Parse the date
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gr.date = simpleDateFormat.parse(results.getString(3));

                // Add to the user's object
                player.gameHistory.add(gr);
            }

            return player;

        } catch (Exception e) {
            // User not found.
            e.printStackTrace();
        }
        return null;

    }

    // Fully Tested
    // Update game status - function that is called twice per game to save the player's state
    @Override
    public void updateGameStats(Player you, Player opponent, int won, String UUID) {
        
        // Query
        String q = "INSERT INTO games (PlayerOne, PlayerTwo, Win, Date, GameUUID) VALUES (?,?,?,?,?)";

        // Convert to an int
        int IntWon = (won);

        try {
            // Prepared insert statement
            PreparedStatement ps = conn.prepareStatement(q);

            // Date format for some standard
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // filling prepared statement
            ps.setInt(1, you.id);
            ps.setInt(2, opponent.id);
            ps.setInt(3, IntWon);
            ps.setString(4, simpleDateFormat.format(new Date()));
            ps.setString(5, UUID);
            ps.execute();

        } catch (Exception e) {

        }
    }

    // Function that can be called if the user only wants to recieve the entire data set
    @Override
    public ObservableList<ObservableList<String>> getGames() {
        return getGames(null);
    }

    // Function to generate the game history tableview array list
    @Override
    public ObservableList<ObservableList<String>> getGames(String player) {

        // Return data
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        // Prepared statement
        PreparedStatement ps;
        try {
            
            if (player == null) {
                // They want to get all players
                ps = conn.prepareStatement("SELECT iif( games.win=-1, 'Tie!', (SELECT username FROM users WHERE id = games.Win)) AS Winner, iif ( games.win=-1,  (SELECT username FROM users WHERE id = games.PlayerOne) & ',' & (SELECT username FROM users WHERE id = PlayerTwo),   ( SELECT username FROM users WHERE id = iif( games.Win=games.PlayerOne, games.PlayerTwo, games.PlayerOne))) AS Loser, Date FROM games");
                
                //ps = conn.prepareStatement("SELECT s.GameUUID, s.WhoWon, s.WhoLost, s.Date FROM ( SELECT GameUUID, iif(games.Win=1, (SELECT username FROM users WHERE games.PlayerOne=id), (SELECT username FROM users WHERE games.PlayerTwo=id)) AS WhoWon, iif(games.Win=0, (SELECT username FROM users WHERE games.PlayerOne=id), (SELECT username FROM users WHERE games.PlayerTwo=id)) AS WhoLost, Date FROM games) as s  GROUP BY s.GameUUID, s.WhoWon, s.WhoLost,  s.Date ;");
            } else {
                // They only want a certain player
                ps = conn.prepareStatement("SELECT * FROM (SELECT iif( games.win=-1, 'Tie!', (SELECT username FROM users WHERE id = games.Win)) AS Winner, iif ( games.win=-1,  (SELECT username FROM users WHERE id = games.PlayerOne) & ',' & (SELECT username FROM users WHERE id = PlayerTwo),   ( SELECT username FROM users WHERE id = iif( games.Win=games.PlayerOne, games.PlayerTwo, games.PlayerOne))) AS Loser, Date FROM games) AS f WHERE f.Winner = ? OR f.Loser LIKE ?;");
                ps.setString(1, player);
                ps.setString(2, "%" + player + "%");
            }

            // Results from the prepared statement
            ResultSet results = ps.executeQuery();

            // Loop through them
            while (results.next()) {
                // Row array list
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(results.getString(1));
                row.add(results.getString(2));
                row.add(results.getString(3));
                data.add(row);
            }

        } catch (Exception ex) {
            Logger.getLogger(AccessController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;

    }

    
    //Function to generate the leaderboard tableview array list
    public ObservableList<ObservableList<String>> getLeaderboard() {

        // Data to return
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try {
            // Return a list of all the users their won games, lost games, and win percentage
            PreparedStatement ps = conn.prepareStatement("SELECT f.username, f.wins, f.totalgames FROM (SELECT username, (SELECT COUNT(PlayerOne) FROM games WHERE win=users.id  ) AS WINS, (SELECT COUNT(PlayerOne) FROM games WHERE PlayerOne=users.id OR PlayerTwo=users.id) AS totalgames FROM users ) as f;");

            ResultSet results = ps.executeQuery();

            while (results.next()) {

                // Table row arraylist
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(results.getString(1));
                row.add(results.getString(2));
                row.add(results.getString(3));
                
                // Whoops Ucanaccess can't return my calculated field... :(
                row.add(String.valueOf((Double.valueOf(results.getString(2)) / Double.valueOf(results.getString(3))) * 100));

                data.add(row);
            }

        } catch (SQLException ex) {
            Logger.getLogger(AccessController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

}
