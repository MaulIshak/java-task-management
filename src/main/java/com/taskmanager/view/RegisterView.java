package com.taskmanager.view;

import com.taskmanager.service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

        Label titleLabel = new Label("Register");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setMaxWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            try {
                authService.register(nameField.getText(), emailField.getText(), passwordField.getText());
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setText("Registration successful! Please login.");
            } catch (Exception ex) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText(ex.getMessage());
            }
        });

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setOnAction(e -> mainLayout.showLogin());

        getChildren().addAll(titleLabel, nameField, emailField, passwordField, registerButton, backToLoginButton,
                errorLabel);
    }
}
