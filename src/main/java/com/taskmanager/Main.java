package com.taskmanager;

import com.taskmanager.model.User;
import com.taskmanager.service.AuthService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.Scanner;

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

    public static void main(String[] args) {

        // --- TEST BACKEND LOGIC ---
        System.out.println("=== SYSTEM START (CONSOLE TEST) ===");
        AuthService authService = new AuthService();
        Scanner scanner = new Scanner(System.in);

        try {
            // Skenario 1: Register User Baru
            System.out.println("\n--- TESTING REGISTER ---");
            String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
            System.out.println("Registering user: " + uniqueEmail);

            User registeredUser = authService.register("Test User", uniqueEmail, "password123");
            System.out.println("SUCCESS: User registered with ID: " + registeredUser.getId());
            System.out.println("Hashed Password in DB: " + registeredUser.getPasswordHash());

            // Skenario 2: Login Salah Password
            System.out.println("\n--- TESTING LOGIN (FAIL) ---");
            try {
                authService.login(uniqueEmail, "wrongpassword");
            } catch (Exception e) {
                System.out.println("EXPECTED ERROR: " + e.getMessage());
            }

            // Skenario 3: Login Sukses
            System.out.println("\n--- TESTING LOGIN (SUCCESS) ---");
            User loggedInUser = authService.login(uniqueEmail, "password123");
            System.out.println("SUCCESS: Welcome back, " + loggedInUser.getName());

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== BACKEND TEST FINISHED, LAUNCHING JAVAFX ===");

        // Launch JavaFX (Nanti UI akan dibangun di method start)
        launch(args);
    }

}