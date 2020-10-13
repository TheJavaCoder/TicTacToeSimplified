package com.tictactoe.server;

import com.tictactoe.server.Database.DBController;
import com.tictactoe.server.Database.Providers.AccessController;
import com.tictactoe.server.game.Player;
import com.tictactoe.server.game.TicTacToe;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Authored by: Bailey Costello
 * 2020
 */
public class App extends Application {

    TextArea debug = new TextArea();

    ServerSocket socket;

    ArrayList<Player> waitingPlayers = new ArrayList<>();

    private DBController db;
    
    ArrayList<Stage> extraStages = new ArrayList<>();
    
    // History table view and search field needs to be global cause that button 
    TextField search;
    TableView builtTable;

    @Override
    public void start(Stage stage) {
        
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        debug.setPadding(new Insets(8));
        debug.setEditable(false);

        
        // Game history view
        Button b = new Button("View Game History");
        b.setPadding(new Insets(8));
        b.setOnAction((t) -> {
            displayHistory();
        });

        // Leaderboard view
        Button b2 = new Button("View Leaderboard");
        b2.setPadding(new Insets(8));
        b2.setOnAction((t) -> {
            displayLeaderBoard();
        });

        // Styling for those buttons
        HBox h = new HBox();
        h.setAlignment(Pos.CENTER);
        h.getChildren().addAll(b, b2);

        
        vbox.getChildren().addAll(debug, h);

        var scene = new Scene(vbox, 350, 200);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
        
        // Clean that server socket up!
        stage.setOnCloseRequest((t) -> {
            
            for(Stage exs : extraStages) {
                exs.close();
            }
            
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // Start listening for clients
        listenForClients();
    }

    public void listenForClients() {

        new Thread(() -> {

            try {
                socket = new ServerSocket(2000);

                Platform.runLater(() -> {
                    debug.appendText("Server started on port 2000... \nListening for clients...");
                });

                while (socket != null) {
                    Socket client = socket.accept();

                    String playerName = new DataInputStream(client.getInputStream()).readUTF();

                    debug.appendText("\nUser: " + playerName);

                    db.addPlayer(playerName);

                    Player p = db.getPlayer(playerName);

                    p.connection = client;

                    new ObjectOutputStream(client.getOutputStream()).writeObject(p);

                    waitingPlayers.add(p);

                    if (waitingPlayers.size() >= 2) {
                        //TODO need to spin up a game thread...

                        debug.appendText("\nStarting a game!");

                        Player p1 = waitingPlayers.get(0);
                        Player p2 = waitingPlayers.get(1);

                        Thread t = new Thread(new TicTacToe(p1, p2));

                        t.start();

                        //waitingPlayers.removeAll(selectedPlayers);
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

    }

    public void removePlayers(List<Player> arr) {
        for (Player p : arr) {
            waitingPlayers.remove(p);
        }
    }

    public void displayHistory() {
        search = new TextField();
        
        // Fill the table created earilier.
        builtTable = buildTable("Winner", "Loser", "Date");
        builtTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        builtTable.setItems(db.getGames());
        
        // Main
        VBox secondary = new VBox();
        
        //Button container
        HBox secon = new HBox();
        secon.setAlignment(Pos.CENTER);
        
        Button searchBT = new Button("Search");
        search.setPromptText("Enter username");

        
        // Search button
        searchBT.setOnAction((e) -> {
            builtTable.getItems().clear();
            if (search.getText().length() != 0) {
                builtTable.setItems(db.getGames(search.getText()));
            } else {
                builtTable.setItems(db.getGames());
            }
        });

        // Add button and input container to the main view
        secon.getChildren().addAll(search, searchBT);

        // Add container and table together
        secondary.getChildren().addAll(secon, builtTable);

        Scene secondScene = new Scene(secondary, 260, 200);
        Stage newWindow = new Stage();
        newWindow.setTitle("Game History");
        newWindow.setScene(secondScene);
        extraStages.add(newWindow);
        newWindow.show();
    }

    public void displayLeaderBoard() {
        VBox secondary = new VBox();

        // Leaderboard table view
        TableView leaderboard = buildTable("User", "Wins", "Total Games", "Win Percentage");
        leaderboard.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                

        // filling table and setting sorting order
        leaderboard.setItems(db.getLeaderboard());
        leaderboard.getSortOrder().add(leaderboard.getColumns().get(3));
        leaderboard.sort();

        secondary.getChildren().add(leaderboard);

        Scene secondScene = new Scene(secondary, 260, 200);
        Stage newWindow = new Stage();
        newWindow.setTitle("Leaderboard");
        newWindow.setScene(secondScene);
        extraStages.add(newWindow);
        newWindow.show();
    }

    // Function used to build tableview column objects basaed on a list of column names.
    private TableView buildTable(String... columns) {
        TableView tableview = new TableView();

        for (int i = 0; i < columns.length; i++) {

            final int j = i;

            TableColumn col = new TableColumn(columns[i]);
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j));
                }
            });
            col.setComparator(col.getComparator().reversed());
            tableview.getColumns().add(col);

            Callback<TableColumn<Map, String>, TableCell<Map, String>> cellFactoryForMap
                    = new Callback<TableColumn<Map, String>, TableCell<Map, String>>() {
                @Override
                public TableCell call(TableColumn p) {
                    return new TextFieldTableCell(new StringConverter() {
                        @Override
                        public String toString(Object t) {
                            return t.toString();
                        }

                        @Override
                        public Object fromString(String string) {
                            return string;
                        }
                    });
                }
            };

            if (j != 0) {
                col.setCellFactory(cellFactoryForMap);
            }
        }

        return tableview;
    }

    public static void main(String[] args) {
        launch();
    }

    public void init() {
        db = new AccessController();
        db.init();
    }

    public DBController getDB() {
        return this.db;
    }

}
