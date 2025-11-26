package com.taskmanager.view;

import com.taskmanager.service.AuthService;
import com.taskmanager.util.UserSession;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            try {
                authService.login(emailField.getText(), passwordField.getText());
                // Observer in MainLayout will handle the view switch
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        Button goToRegisterButton = new Button("Don't have an account? Register");
        goToRegisterButton.setOnAction(e -> mainLayout.showRegister());

        getChildren().addAll(titleLabel, emailField, passwordField, loginButton, goToRegisterButton, errorLabel);
    }
}
