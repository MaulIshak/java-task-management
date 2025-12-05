package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;

public class CreateProjectModal {

    private static final Logger LOGGER = Logger.getLogger(CreateProjectModal.class.getName());
    private static final String LABEL_STYLE = "-fx-font-size: 14px; -fx-text-fill: #666;";

    private final ProjectService projectService;
    private final OrganizationService organizationService;
    private final Runnable onSuccess;
    private Organization preSelectedOrg;

    public CreateProjectModal(ProjectService projectService, OrganizationService organizationService,
            Runnable onSuccess) {
        this(projectService, organizationService, null, onSuccess);
    }

    public CreateProjectModal(ProjectService projectService, OrganizationService organizationService,
            Organization preSelectedOrg, Runnable onSuccess) {
        this.projectService = projectService;
        this.organizationService = organizationService;
        this.preSelectedOrg = preSelectedOrg;
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
        dialogVbox.setMaxSize(400, 400);
        dialogVbox.setAlignment(Pos.TOP_LEFT);
        dialogVbox.setPadding(new Insets(30));
        dialogVbox.setStyle(
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-background-radius: 10;");

        Label titleLabel = new Label("Create Project");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        ComboBox<Organization> orgComboBox = createOrgComboBox();
        VBox orgGroup = new VBox(5);
        Label orgLabel = new Label("Organization");
        orgLabel.setStyle(LABEL_STYLE);
        orgGroup.getChildren().addAll(orgLabel, orgComboBox);

        TextField nameField = createNameField();
        VBox nameGroup = new VBox(5);
        Label nameLabel = new Label("Project Name");
        nameLabel.setStyle(LABEL_STYLE);
        nameGroup.getChildren().addAll(nameLabel, nameField);

        TextArea descArea = createDescArea();
        VBox descGroup = new VBox(5);
        Label descLabel = new Label("Description");
        descLabel.setStyle(LABEL_STYLE);
        descGroup.getChildren().addAll(descLabel, descArea);

        HBox buttonBox = createButtonBox(dialog, orgComboBox, nameField, descArea);

        dialogVbox.getChildren().addAll(titleLabel, orgGroup, nameGroup, descGroup, buttonBox);
        root.getChildren().add(dialogVbox);

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(dialogScene);

        positionDialog(dialog);

        dialog.show();
    }

    private ComboBox<Organization> createOrgComboBox() {
        ComboBox<Organization> orgComboBox = new ComboBox<>();
        orgComboBox.setMaxWidth(Double.MAX_VALUE);
        try {
            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();
            orgComboBox.getItems().addAll(orgs);

            if (preSelectedOrg != null) {
                for (Organization org : orgs) {
                    if (org.getId() == preSelectedOrg.getId()) {
                        orgComboBox.getSelectionModel().select(org);
                        break;
                    }
                }
            } else if (!orgs.isEmpty()) {
                orgComboBox.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading organizations", e);
        }

        orgComboBox.setConverter(new StringConverter<Organization>() {
            @Override
            public String toString(Organization object) {
                return object != null ? object.getOrgName() : "";
            }

            @Override
            public Organization fromString(String string) {
                return null;
            }
        });
        return orgComboBox;
    }

    private TextField createNameField() {
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Website Redesign");
        nameField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        return nameField;
    }

    private TextArea createDescArea() {
        TextArea descArea = new TextArea();
        descArea.setPromptText("Project description...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        return descArea;
    }

    private HBox createButtonBox(Stage dialog, ComboBox<Organization> orgComboBox, TextField nameField,
            TextArea descArea) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button createBtn = new Button("Create");
        createBtn.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        createBtn.setOnAction(e -> handleCreateProject(dialog, orgComboBox, nameField, descArea));

        buttonBox.getChildren().addAll(cancelBtn, createBtn);
        return buttonBox;
    }

    private void handleCreateProject(Stage dialog, ComboBox<Organization> orgComboBox, TextField nameField,
            TextArea descArea) {
        String name = nameField.getText();
        Organization selectedOrg = orgComboBox.getSelectionModel().getSelectedItem();

        if (selectedOrg != null && name != null && !name.trim().isEmpty()) {
            try {
                projectService.createProject(selectedOrg.getId(), name, descArea.getText());
                if (onSuccess != null) {
                    onSuccess.run();
                }
                dialog.close();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error creating project", ex);
            }
        }
    }

    private void positionDialog(Stage dialog) {
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
    }
}
