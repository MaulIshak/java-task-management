package com.taskmanager.view;

import com.taskmanager.service.OrganizationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateOrganizationModal {

    private static final Logger LOGGER = Logger.getLogger(CreateOrganizationModal.class.getName());

    private final OrganizationService organizationService;
    private final Runnable onSuccess;

    public CreateOrganizationModal(OrganizationService organizationService, Runnable onSuccess) {
        this.organizationService = organizationService;
        this.onSuccess = onSuccess;
    }

    public void show() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // Close on backdrop click
        root.setOnMouseClicked(e -> {
            if (e.getTarget() == root) {
                dialog.close();
            }
        });

        VBox dialogVbox = new VBox(20);
        dialogVbox.setMaxSize(400, 250);
        dialogVbox.setAlignment(Pos.TOP_LEFT);
        dialogVbox.setPadding(new Insets(30));
        dialogVbox.setStyle(
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-background-radius: 10;");

        Label titleLabel = new Label("Create Organization");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox inputGroup = new VBox(5);
        Label nameLabel = new Label("Organization Name");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Acme Corp");
        nameField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        inputGroup.getChildren().addAll(nameLabel, nameField);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button createBtn = new Button("Create");
        createBtn.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        createBtn.setOnAction(e -> {
            String name = nameField.getText();
            if (name != null && !name.trim().isEmpty()) {
                try {
                    organizationService.createOrganization(name);
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                    dialog.close();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error creating org", ex);
                }
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, createBtn);

        dialogVbox.getChildren().addAll(titleLabel, inputGroup, buttonBox);
        root.getChildren().add(dialogVbox);

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(dialogScene);

        // Full screen backdrop
        Optional<Window> owner = Window.getWindows().stream().filter(Window::isShowing).findFirst();
        if (owner.isPresent()) {
            Window window = owner.get();
            dialog.setX(window.getX());
            dialog.setY(window.getY());
            dialog.setWidth(window.getWidth());
            dialog.setHeight(window.getHeight());
        } else {
            dialog.setWidth(800);
            dialog.setHeight(600);
        }

        dialog.show();
    }
}
