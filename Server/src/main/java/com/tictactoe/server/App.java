package com.tictactoe.server;

import com.tictactoe.server.Database.DBController;
import com.tictactoe.server.Database.Providers.AccessController;
import com.tictactoe.server.game.Player;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    TextArea debug = new TextArea();
    
    ServerSocket socket;
    
    ArrayList<Socket> waitingPlayers = new ArrayList<>();
    
    private DBController db;
    
    @Override
    public void start(Stage stage) {
        
        stage.setOnCloseRequest((t) -> {
            try { 
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        
        debug.setPadding(new Insets(8));
        debug.setEditable(false);
        
        Button b  = new Button("View Leaderboard");
        b.setPadding(new Insets(8));
        b.setOnAction((t) -> {
            displayLeaderboard();
        });
        
        vbox.getChildren().addAll(debug, b);
        
        var scene = new Scene(vbox, 350, 200);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((t) -> {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        listenForClients();
    }
    
    public void listenForClients() {
    
        new Thread(() -> {
        
            try {
                socket = new ServerSocket(2000);
                
                Platform.runLater(() -> {
                    debug.appendText("Server started on port 2000... \nListening for clients...");
                });
            
                while(socket != null) {
                    Socket client = socket.accept();
                    Platform.runLater(() -> {
                        debug.appendText("\nConnected a client!");
                        
                    });
                    
                    String playerName = new DataInputStream(client.getInputStream()).readUTF();
                    
                    debug.appendText("User: " + playerName);
                    
                    db.addPlayer(playerName);
                    
                    Player p = db.getPlayer(playerName);
                    
                    new ObjectOutputStream(client.getOutputStream()).writeObject(p);
                    
                    waitingPlayers.add(client);
                    
                    if(waitingPlayers.size() == 2) {
                        //TODO need to spin up a game thread...
                        
                        waitingPlayers = new ArrayList<>();
                    }else {
                        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                        dos.writeUTF("INFO: Waiting for another player");
                        dos.writeUTF(STYLESHEET_MODENA);
                    }
                    
                }
            
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
        
    }

    public void displayLeaderboard() {
        StackPane secondary = new StackPane();
        
        Scene secondScene = new Scene(secondary, 230, 100);
        Stage newWindow = new Stage();
        newWindow.setTitle("Leaderboard");
        newWindow.setScene(secondScene);
        newWindow.show();
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