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
    private final MainLayout mainLayout;
    private final AuthService authService;

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

        // Name Field
        Label nameLabel = createStyledLabel("Name");
        HBox nameBox = new HBox(nameLabel);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = createStyledTextField("Name");

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

        // Confirm Password Field
        Label confirmPasswordLabel = createStyledLabel("Confirm Password");
        HBox confirmPasswordBox = new HBox(confirmPasswordLabel);
        confirmPasswordBox.setAlignment(Pos.CENTER_LEFT);

        PasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");

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
        registerButton.setStyle(
                "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");

        registerButton.setOnAction(e -> {
            try {
                authService.register(nameField.getText(), emailField.getText(), passwordField.getText());
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setText("Registration successful!");

                // Use a separate thread to wait before switching view to avoid blocking UI
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> mainLayout.showLogin());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();

            } catch (Exception ex) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText(ex.getMessage());
            }
        });

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setOnAction(e -> mainLayout.showLogin());
        backToLoginButton.setMaxWidth(Double.MAX_VALUE);
        backToLoginButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        card.getChildren().addAll(hbox, nameBox, nameField, emailBox, emailField, passwordBox, passwordField,
                confirmPasswordBox, confirmPasswordField, registerButton, backToLoginButton, errorLabel);
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
                "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border: 1px solid lightgray; -fx-box-shadow: none;");
        field.getStyleClass().add("text-field");
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border: 1px solid lightgray; -fx-box-shadow: none;");
        field.getStyleClass().add("text-field");
        return field;
    }
}
