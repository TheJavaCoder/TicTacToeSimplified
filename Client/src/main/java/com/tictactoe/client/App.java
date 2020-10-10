package com.tictactoe.client;

import com.tictactoe.server.Database.GameResult;
import com.tictactoe.server.game.Player;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
        vbx.setAlignment(Pos.CENTER);

        Label connecting = new Label("Waiting on another player to join the game");
        
        vbx.getChildren().add(connecting);

        vbx.getChildren().add(new Label("Win rate: " + (player.winPercentage() * 100 ) + "%"));

        TableView tableview = new TableView();

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        ArrayList<String> columns = new ArrayList<>();
        columns.add("opponent");
        columns.add("date");
        columns.add("won");

        for (int i = 0; i < columns.size(); i++) {

            final int j = i;

            TableColumn col = new TableColumn(columns.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j));
                }
            });

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

            if (j != 1) {
                col.setCellFactory(cellFactoryForMap);
            }
        }

        for (GameResult gr : player.gameHistory) {
            ObservableList<String> row  = FXCollections.observableArrayList();
            row.add(gr.opponent);
            row.add(gr.date.toString());
            row.add(gr.won ? "You" : "Them");
            data.add(row);
        }
        tableview.setItems(data);

        vbx.getChildren().add(tableview);

        Scene scene = new Scene(vbx, 350, 300);
        s.setTitle("Game History");
        s.setScene(scene);
        
        new Thread(() -> {
        
            try {
                String readyToPlay = new DataInputStream(socket.getInputStream()).readUTF();
                
                if("Start_Game".equals(readyToPlay)) {
                    Platform.runLater(() -> {
                        connecting.setText("Connected!");
                    });
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }).start();
        
        
    }

    public static void main(String[] args) {
        launch();
    }

}
