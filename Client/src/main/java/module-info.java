module com.tictactoe.client {
    requires javafx.controls;
    exports com.tictactoe.client;
    opens com.tictactoe.server.Database to javafx.base;
}
