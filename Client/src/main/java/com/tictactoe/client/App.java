package com.tictactoe.client;

import com.tictactoe.server.game.Player;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    Socket socket;
    
    Player player;

    @Override
    public void start(Stage stage) {

        VBox vbx = new VBox();
        vbx.setAlignment(Pos.CENTER);
        vbx.setPadding(new Insets(8));

        TextField username = new TextField();
        username.setAlignment(Pos.CENTER);
        TextField server = new TextField();
        server.setAlignment(Pos.CENTER);

        Button conn = new Button("Connect");
        conn.setOnAction((t) -> {
            connect(stage, username.getText(), server.getText());
        });

        vbx.getChildren().addAll(new Label("Please enter your name"), username, new Label("Enter server address"), server, conn);

        var scene = new Scene(vbx, 300, 125);
        stage.setScene(scene);
        stage.show();
    }

    public void connect(Stage s, String username, String server) {

        VBox vbx = new VBox();
        vbx.setAlignment(Pos.CENTER);
        vbx.setPadding(new Insets(8));

        Label connecting = new Label("Attempting to connect..");
        vbx.getChildren().add(connecting);

        Scene scene = new Scene(vbx, 300, 100);
        Stage window = new Stage();
        
        window.setOnCloseRequest((t) -> {
           
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        });
        
        window.setScene(scene);
        window.show();

        try {
            socket = new Socket(server, 2000);

            // Send the username to the server
            new DataOutputStream(socket.getOutputStream()).writeUTF(username);

            player = (Player) new ObjectInputStream(socket.getInputStream()).readObject();
            
            Platform.runLater(() -> {
                connecting.setText("Loaded: " + player.name);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                window.close();
                showHistory(s);
            });
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void showHistory(Stage s) {
    
        VBox vbx = new VBox();
        vbx.setPadding(new Insets(8));
        
        vbx.getChildren().add(new Label("Win rate: " + player.winPercentage()));
        
        Scene scene = new Scene(vbx, 350,300);
        s.setTitle("Game History");
        s.setScene(scene);
        
    }

    public static void main(String[] args) {
        launch();
    }

}
