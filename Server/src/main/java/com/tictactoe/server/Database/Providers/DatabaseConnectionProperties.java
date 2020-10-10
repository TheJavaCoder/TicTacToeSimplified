/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tictactoe.server.Database.Providers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConnectionProperties {

    @JsonProperty("host")
    String host;
    
    @JsonProperty("port")
    int port;
    
    @JsonProperty("user")
    String user;
    
    @JsonProperty("pass")
    String pass;
    
}
