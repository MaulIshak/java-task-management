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

public class RegisterView extends VBox implements View {
    private final AuthService authService;

    public RegisterView(AuthService authService) {
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

        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle(
                "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");

        registerButton.setOnAction(e -> {
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                confirmPasswordErrorLabel.setText("Passwords do not match");
                return;
            }
            try {
                authService.register(nameField.getText(), emailField.getText(), passwordField.getText());
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setText("Registration successful!");

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> MainLayout.getInstance().showLogin());
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        ex.printStackTrace();
                    }
                }).start();

            } catch (Exception ex) {
                errorLabel.setTextFill(Color.RED);
                errorLabel.setText(ex.getMessage());
            }
        });

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setOnAction(e -> MainLayout.getInstance().showLogin());
        backToLoginButton.setMaxWidth(Double.MAX_VALUE);
        backToLoginButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        card.getChildren().addAll(hbox, nameBox, nameField, emailBox, emailField, passwordBox, passwordField,
                confirmPasswordBox, confirmPasswordField, confirmPasswordErrorLabel, registerButton, backToLoginButton,
                errorLabel);
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
