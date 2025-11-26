package com.taskmanager;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.service.AuthService;
import com.taskmanager.service.TaskService;
import com.taskmanager.view.MainLayout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("JavaFX Documentation App");

        MainLayout mainLayout = new MainLayout();
        Scene scene = new Scene(mainLayout, 1024, 768);

        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Launch JavaFX (Nanti UI akan dibangun di method start)
        launch(args);
    }

}