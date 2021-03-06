package DatabaseTests;

/*

Output of the test

---------------------- Database Controller Tests ---------------------- 

Loaded Access Driver!
Access Database connected!
users table already exists... Skipping
games table already exists... Skipping
User: Test1' already exists... Skipping 
User: Test2 already exists... Skipping 
----------------------- Game History of Test1 ------------------------
Matches Played: 14 

Win Percentage: 0.9285714285714286 


Match #1
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #2
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #3
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #4
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #5
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #6
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #7
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #8
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #9
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #10
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #11
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #12
Opponent: Test2
Who won?: You
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #13
Opponent: Test2
Who won?: You
Date of match: Fri Oct 02 00:00:00 EDT 2020

Match #14
Opponent: Test2
Who won?: Them
Date of match: Fri Oct 02 00:00:00 EDT 2020

----------------------- Game History of Test2 ------------------------
Matches Played: 14 

Win Percentage: 0.07142857142857142 


Match #1
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #2
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #3
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #4
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #5
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #6
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #7
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #8
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #9
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #10
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #11
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #12
Opponent: Test1
Who won?: Them
Date of match: Wed Sep 30 00:00:00 EDT 2020

Match #13
Opponent: Test1
Who won?: Them
Date of match: Fri Oct 02 00:00:00 EDT 2020

Match #14
Opponent: Test1
Who won?: You
Date of match: Fri Oct 02 00:00:00 EDT 2020


 */
/**
 * Authored by: Bailey Costello 2020
 */
import com.tictactoe.server.Database.Providers.AccessController;
import com.tictactoe.server.Database.DBController;
import com.tictactoe.server.Database.GameResult;
import com.tictactoe.server.game.Player;
import java.util.UUID;

// Test for generatation of the db file and tables. 
public class AccessControllerTest {

    public static void main(String[] args) {

        System.out.println("---------------------- Database Controller Tests ---------------------- \n");

        DBController db = new AccessController();

        db.init();

        String player1 = "Test3";
        String player2 = "Test1";

        db.addPlayer(player1);

        db.addPlayer(player2);

        UUID uuid = UUID.randomUUID();

        // Player object, Player object, id of player that won if tie -1, game uuid
        db.updateGameStats(db.getPlayer(player1), db.getPlayer(player2), 1, uuid.toString());

        printGameHistory(db.getPlayer(player1));
        printGameHistory(db.getPlayer(player2));
    }

    public static void printGameHistory(Player p) {

        System.out.println("----------------------- Game History of " + p.name + " ------------------------");

        System.out.println("Matches Played: " + p.gameHistory.size() + " \n");

        System.out.println("Win Percentage: " + p.winPercentage() + " \n\n");

        for (int i = 0; i < p.gameHistory.size(); i++) {

            System.out.println("Match #" + (i + 1));

            GameResult g = p.gameHistory.get(i);

            System.out.println("Opponent: " + g.opponent);
            System.out.print("Who won?: ");
            if (g.won != -1) {
                System.out.println((g.won) == p.id ? "You" : "Them");
            } else {
                System.out.println("Tie!");
            }
            System.out.println("Date of match: " + g.date + "\n");
        }

    }
}
