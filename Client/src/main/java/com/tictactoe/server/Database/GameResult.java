package com.tictactoe.server.Database;

import java.io.Serializable;
import java.util.Date;
import javafx.beans.property.StringProperty;

public class GameResult implements Serializable {

    // who the player was playing
    public String opponent;

    // Date of the match
    public Date date;

    // did the user win the match?
    public int won;

    
}
