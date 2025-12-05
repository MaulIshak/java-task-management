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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JoinOrganizationModal {

    private final OrganizationService organizationService;
    private final Runnable onSuccess;

    public JoinOrganizationModal(OrganizationService organizationService, Runnable onSuccess) {
        this.organizationService = organizationService;
        this.onSuccess = onSuccess;
    }

    public void show() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        root.setOnMouseClicked(e -> {
            if (e.getTarget() == root) {
                dialog.close();
            }
        });

        VBox dialogVbox = new VBox(15);
        dialogVbox.setMaxSize(350, 250);
        dialogVbox.setAlignment(Pos.TOP_LEFT);
        dialogVbox.setPadding(new Insets(30));
        dialogVbox.setStyle(
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-background-radius: 10;");

        Label titleLabel = new Label("Join Organization");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox codeGroup = new VBox(5);
        Label codeLabel = new Label("Organization Code");
        codeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        TextField codeField = new TextField();
        codeField.setPromptText("Enter organization code");
        codeField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        codeGroup.getChildren().addAll(codeLabel, codeField);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");
        cancelButton.setOnAction(e -> dialog.close());

        Button joinButton = new Button("Join");
        joinButton.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        joinButton.setOnAction(e -> {
            try {
                organizationService.joinOrganization(codeField.getText());
                if (onSuccess != null) {
                    onSuccess.run();
                }
                dialog.close();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(cancelButton, joinButton);

        dialogVbox.getChildren().addAll(titleLabel, codeGroup, errorLabel, buttonBox);
        root.getChildren().add(dialogVbox);

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(dialogScene);

        if (Stage.getWindows().stream().anyMatch(javafx.stage.Window::isShowing)) {
            javafx.stage.Window owner = Stage.getWindows().stream().filter(javafx.stage.Window::isShowing).findFirst()
                    .orElse(null);
            if (owner != null) {
                dialog.setX(owner.getX());
                dialog.setY(owner.getY());
                dialog.setWidth(owner.getWidth());
                dialog.setHeight(owner.getHeight());
            }
        } else {
            dialog.setWidth(800);
            dialog.setHeight(600);
        }

        dialog.show();
    }
}
