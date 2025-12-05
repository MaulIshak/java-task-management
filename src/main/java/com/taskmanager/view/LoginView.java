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

public class LoginView extends VBox implements View {
    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
        render();
    }

    @Override
    public void render() {
        getChildren().clear();
        setAlignment(Pos.CENTER);
        setSpacing(10);

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

        // Email Field
        Label emailLabel = createStyledLabel("Email");
        HBox emailBox = new HBox(emailLabel);
        emailBox.setAlignment(Pos.CENTER_LEFT);

        TextField emailField = createStyledTextField("Email");

        // Password Field
        Label passwordLabel = createStyledLabel("Password");
        HBox passwordBox = new HBox(passwordLabel);
        passwordBox.setAlignment(Pos.CENTER_LEFT);

        PasswordField passwordField = createStyledPasswordField("Password");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
                "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");
        loginButton.setOnAction(e -> {
            try {
                authService.login(emailField.getText(), passwordField.getText());
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        Button goToRegisterButton = new Button("Don't have an account? Register");
        goToRegisterButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand;");
        goToRegisterButton.setOnAction(e -> MainLayout.getInstance().showRegister());

        card.getChildren().addAll(titleBox, emailBox, emailField, passwordBox, passwordField, loginButton,
                goToRegisterButton, errorLabel);
        getChildren().add(card);
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        return label;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: lightgray; -fx-border-width: 1; -fx-border-style: solid;");
        field.getStyleClass().add("text-field");
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: lightgray; -fx-border-width: 1; -fx-border-style: solid;");
        field.getStyleClass().add("text-field");
        return field;
    }
}
