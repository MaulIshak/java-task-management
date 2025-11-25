package com.taskmanager;

import com.taskmanager.util.DBConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();

//        var stmt = conn.prepareStatement("SELECT * FROM users");
//        var rs = stmt.executeQuery();
//
//        while (rs.next()) {
//            System.out.println(rs.getString("name"));
//        }

        launch();
    }

}