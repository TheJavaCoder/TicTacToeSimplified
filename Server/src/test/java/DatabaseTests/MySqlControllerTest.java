package DatabaseTests;


import com.tictactoe.server.Database.DBController;
import com.tictactoe.server.Database.Providers.MySqlController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bapco
 */
public class MySqlControllerTest {

    public static void main(String[] args) {
        
        DBController db = new MySqlController("C:\\Users\\bapco\\Documents\\Java3\\GroupProject_tic_tac_toe\\TicTacToe_Server\\resources\\DatabaseConnectionProperties.yml");
        
        db.init();
        
    }

}
