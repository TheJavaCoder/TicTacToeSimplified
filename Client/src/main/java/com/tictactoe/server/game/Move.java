/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tictactoe.server.game;

import java.io.Serializable;

/**
 * Authored by: Bailey Costello
 * 2020
 */
public class Move implements Serializable{

    public int tileX, tileY;
 
    public Move(int x, int y) {
        tileX = x;
        tileY = y;
    }
    
}
