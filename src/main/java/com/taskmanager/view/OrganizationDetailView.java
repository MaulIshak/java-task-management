package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class OrganizationDetailView extends VBox {

    private final OrganizationService organizationService;
    private final ProjectService projectService;
    private final int organizationId;
    private Organization organization;

    public OrganizationDetailView(int organizationId, OrganizationService organizationService,
            ProjectService projectService) {
        this.organizationId = organizationId;
        this.organizationService = organizationService;
        this.projectService = projectService;

        setPadding(new Insets(30));
        setSpacing(30);
        getStyleClass().add("dashboard-view");

        loadOrganizationData();
    }

    private void loadOrganizationData() {
        getChildren().clear();
        try {
            organization = organizationService.getOrganizationDetails(organizationId);

            // Header with Org Name and Join Code
            HBox header = new HBox(20);
            header.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(organization.getOrgName());
            nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox codeBox = new VBox(2);
            codeBox.setAlignment(Pos.CENTER_RIGHT);
            Label codeTitle = new Label("Join Code");
            codeTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

            HBox codeDisplay = new HBox(10);
            codeDisplay.setAlignment(Pos.CENTER_RIGHT);
            TextField codeField = new TextField(organization.getCode());
            codeField.setEditable(false);
            codeField.setStyle(
                    "-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 3; -fx-padding: 5; -fx-pref-width: 100px; -fx-alignment: center;");

            codeDisplay.getChildren().add(codeField);
            codeBox.getChildren().addAll(codeTitle, codeDisplay);

            header.getChildren().addAll(nameLabel, spacer, codeBox);
            getChildren().add(header);

            // Projects Section
            VBox projectSection = createSectionHeader("Projects");
            FlowPane projectContainer = new FlowPane();
            projectContainer.setHgap(20);
            projectContainer.setVgap(20);

            if (organization.getProjects() != null && !organization.getProjects().isEmpty()) {
                for (Project proj : organization.getProjects()) {
                    VBox card = createGenericCard(proj.getName(), proj.getDescription(), "Members: N/A", 0.0);
                    card.setStyle(card.getStyle() + "-fx-cursor: hand;");
                    card.setOnMouseClicked(e -> {
                        if (getScene() != null && getScene().getRoot() instanceof MainLayout) {
                            ((MainLayout) getScene().getRoot()).switchView("Project:" + proj.getId());
                        }
                    });
                    projectContainer.getChildren().add(card);
                }
            } else {
                Label emptyLabel = new Label("No projects yet.");
                projectContainer.getChildren().add(emptyLabel);
            }

            projectSection.getChildren().add(projectContainer);
            getChildren().add(projectSection);

        } catch (Exception e) {
            e.printStackTrace();
            getChildren().add(new Label("Error loading organization details: " + e.getMessage()));
        }
    }

    private VBox createSectionHeader(String title) {
        VBox section = new VBox(15);
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        header.getChildren().add(titleLabel);

        // Add Project Button
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        Button addBtn = new Button("+ Add Project");
        addBtn.setOnAction(e -> showCreateProjectModal());
        header.getChildren().add(addBtn);

        section.getChildren().add(header);
        return section;
    }

    private VBox createGenericCard(String title, String description, String metaInfo, double progress) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(350);
        card.setMinWidth(300);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        card.getChildren().add(titleLabel);

        if (description != null && !description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.getStyleClass().add("card-description");
            descLabel.setWrapText(true);
            card.getChildren().add(descLabel);
        }

        HBox metaBox = new HBox(5);
        Label metaLabel = new Label(metaInfo);
        metaLabel.getStyleClass().add("card-members");
        metaBox.getChildren().add(metaLabel);
        card.getChildren().add(metaBox);

        VBox progressBox = new VBox(5);
        HBox progressLabelBox = new HBox();
        Label progressLabel = new Label("Progress");
        progressLabel.getStyleClass().add("progress-label");
        Label progressValue = new Label((int) (progress * 100) + "%");
        progressValue.getStyleClass().add("progress-value");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        progressLabelBox.getChildren().addAll(progressLabel, spacer, progressValue);

        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("custom-progress-bar");

        progressBox.getChildren().addAll(progressLabelBox, progressBar);
        card.getChildren().add(progressBox);

        return card;
    }

    private void showCreateProjectModal() {
        new CreateProjectModal(projectService, organizationService, this::loadOrganizationData).show();
    }
}
