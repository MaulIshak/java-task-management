package com.taskmanager.view;

import com.taskmanager.service.AuthService;
import com.taskmanager.util.UserSession;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginView extends VBox {
    private MainLayout mainLayout;
    private AuthService authService;

    public LoginView(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        this.authService = new AuthService();

        setAlignment(Pos.CENTER);
        setSpacing(10);
        getStyleClass().add("login-view");

        // card
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setPadding(new javafx.geometry.Insets(30));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(17, 16, 16, 0.1), 10, 0, 0, 5);");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.getChildren().add(titleLabel);

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox emailBox = new HBox();
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.getChildren().addAll(emailLabel);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border: 1px solid lightgray; -fx-box-shadow: none;");
        emailField.getStyleClass().add("text-field");

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox passwordBox = new HBox();
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        passwordBox.getChildren().addAll(passwordLabel);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border: 1px solid lightgray; -fx-box-shadow: none;");
        passwordField.getStyleClass().add("text-field");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");
        loginButton.setOnAction(e -> {
            try {
                authService.login(emailField.getText(), passwordField.getText());
                // Observer in MainLayout will handle the view switch
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        Button goToRegisterButton = new Button("Don't have an account? Register");
        goToRegisterButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand;");
        goToRegisterButton.setOnAction(e -> mainLayout.showRegister());

        card.getChildren().addAll(titleBox, emailBox, emailField, passwordBox, passwordField, loginButton, goToRegisterButton, errorLabel);
        getChildren().add(card);
    }
}
