package com.taskmanager;

import com.taskmanager.view.MainLayout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("JavaFX Documentation App");

        MainLayout mainLayout = MainLayout.getInstance();
        mainLayout.update(); 
        Scene scene = new Scene(mainLayout, 1024, 768);

        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/fonts.css").toExternalForm());
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}