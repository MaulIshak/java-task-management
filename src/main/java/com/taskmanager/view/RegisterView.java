package com.taskmanager.view;

import com.taskmanager.service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class RegisterView extends VBox {
    private MainLayout mainLayout;
    private AuthService authService;

    public RegisterView(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        this.authService = new AuthService();

        setAlignment(Pos.CENTER);
        setSpacing(10);
        getStyleClass().add("register-view");

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

        Label titleLabel = new Label("Register");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(titleLabel);

        Label nameLabel = new Label("Name");
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox nameBox = new HBox();
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.getChildren().addAll(nameLabel);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border: 1px solid lightgray; -fx-box-shadow: none;");
        nameField.getStyleClass().add("text-field");

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

        Label confirmPasswordLabel = new Label("Confirm Password");
        confirmPasswordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox confirmPasswordBox = new HBox();
        confirmPasswordBox.setAlignment(Pos.CENTER_LEFT);
        confirmPasswordBox.getChildren().addAll(confirmPasswordLabel);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);
        confirmPasswordField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border: 1px solid lightgray; -fx-box-shadow: none;");
        confirmPasswordField.getStyleClass().add("text-field");

        Label confirmPasswordErrorLabel = new Label();
        confirmPasswordErrorLabel.setTextFill(Color.RED);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        if (passwordField.getText().equals(confirmPasswordField.getText()) && passwordField.getText().length() >= 8) {
            confirmPasswordErrorLabel.setTextFill(Color.GREEN);
            confirmPasswordErrorLabel.setText("Passwords match");
        } else {
            confirmPasswordErrorLabel.setTextFill(Color.RED);
            confirmPasswordErrorLabel.setText("Passwords do not match");
        }

        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");

        registerButton.setOnAction(e -> {
            try {
                authService.register(nameField.getText(), emailField.getText(), passwordField.getText());
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setText("Registration successful!");

                Thread.sleep(1000);

                mainLayout.showLogin();
            } catch (Exception ex) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText(ex.getMessage());
            }
        });

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setOnAction(e -> mainLayout.showLogin());
        backToLoginButton.setMaxWidth(Double.MAX_VALUE);
        backToLoginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        card.getChildren().addAll(hbox, nameBox, nameField, emailBox, emailField, passwordBox, passwordField, confirmPasswordBox, confirmPasswordField, registerButton, backToLoginButton, errorLabel);
        getChildren().add(card);
    }
}
