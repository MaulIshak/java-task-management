package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import com.taskmanager.service.TaskService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.taskmanager.util.UserSession;
import java.util.List;

public class ProjectView extends VBox implements com.taskmanager.model.interfaces.Observer, View {

    private final ProjectService projectService;
    private final OrganizationService organizationService;
    private final TaskService taskService;

    public ProjectView(ProjectService projectService, OrganizationService organizationService,
            TaskService taskService) {
        this.projectService = projectService;
        this.organizationService = organizationService;
        this.taskService = taskService;

        this.projectService.registerObserver(this);
        UserSession.getInstance().registerObserver(this);

        setPadding(new Insets(30));
        setSpacing(30);
        getStyleClass().add("dashboard-view");

        render();
    }

    @Override
    public void update() {
        javafx.application.Platform.runLater(() -> {
            if (UserSession.getInstance().isLoggedIn()) {
                render();
            } else {
                getChildren().clear();
            }
        });
    }

    @Override
    public void render() {
        getChildren().clear();

        if (!UserSession.getInstance().isLoggedIn()) {
            return;
        }

        Organization currentOrg = AppState.getInstance().getCurrentOrganization();
        if (currentOrg == null) {
            getChildren().add(new Label("No organization selected. Please select an organization from the sidebar."));
            return;
        }

        // Header
        VBox headerSection = createSectionHeader(currentOrg.getOrgName(), true);
        getChildren().add(headerSection);

        // Projects Grid
        FlowPane projectContainer = new FlowPane();
        projectContainer.setHgap(20);
        projectContainer.setVgap(20);
        loadProjects(currentOrg, projectContainer);
        getChildren().add(projectContainer);
    }

    private void loadProjects(Organization org, FlowPane container) {
        try {
            List<Project> projects = projectService.getProjectsByOrganization(org.getId());
            if (projects.isEmpty()) {
                Label emptyLabel = new Label("No projects found in this organization.");
                container.getChildren().add(emptyLabel);
            } else {
                for (Project proj : projects) {
                    // Calculate progress
                    List<Task> tasks = taskService.getTasksByProject(proj.getId());
                    double progress = 0.0;
                    if (!tasks.isEmpty()) {
                        long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
                        progress = (double) doneCount / tasks.size();
                    }

                    container.getChildren().add(createGenericCard(proj.getName(), proj.getDescription(), progress, "Project:" + proj.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            container.getChildren().add(new Label("Error loading projects."));
        }
    }

    private VBox createSectionHeader(String title, boolean showActions) {
        VBox section = new VBox(15);
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        // Title
        Label titleLabel = new Label(title.split(" \\(Join Code:")[0]);
        titleLabel.getStyleClass().add("section-title");
        header.getChildren().add(titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        Organization currentOrg = AppState.getInstance().getCurrentOrganization();
        if (currentOrg != null && currentOrg.getCode() != null && !currentOrg.getCode().isEmpty()) {
            HBox codeBox = new HBox(5);
            codeBox.setAlignment(Pos.CENTER_LEFT);
            codeBox.getStyleClass().add("join-code-box");
            codeBox.setStyle(
                    "-fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 2 8; -fx-background-color: #f9f9f9;");

            Label codeLabel = new Label(currentOrg.getCode());
            codeLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-weight: bold; -fx-text-fill: #555;");

            Button copyBtn = new Button("Copy");
            copyBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #2962ff; -fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 0;");
            copyBtn.setOnAction(e -> {
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(currentOrg.getCode());
                javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                copyBtn.setText("Copied!");
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.seconds(2));
                pause.setOnFinished(ev -> copyBtn.setText("Copy"));
                pause.play();
            });

            codeBox.getChildren().addAll(new Label("Join Code:"), codeLabel, copyBtn);
            header.getChildren().add(codeBox);

            Region btnSpacer = new Region();
            btnSpacer.setMinWidth(10);
            header.getChildren().add(btnSpacer);
        }

        if (showActions && currentOrg != null && organizationService.isCurrentUserOwner(currentOrg.getId())) {
            Button addBtn = new Button("+ Add Project");
            addBtn.getStyleClass().add("primary-button");
            addBtn.setOnAction(e -> showCreateProjectModal());
            header.getChildren().add(addBtn);
        }

        section.getChildren().add(header);
        return section;
    }

    private VBox createGenericCard(String title, String description, double progress, String viewName) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(350);
        card.setMinWidth(300);

        if (viewName != null) {
            card.setStyle(card.getStyle() + "-fx-cursor: hand;");
            card.setOnMouseClicked(e -> {
                try {
                    if (viewName.startsWith("Project:")) {
                        int projId = Integer.parseInt(viewName.split(":")[1]);
                        Project proj = projectService.getProjectWithTasks(projId);
                        AppState.getInstance().setCurrentProject(proj);
                        AppState.getInstance().switchView(ViewName.PROJECT_DETAIL);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        HBox titleBox = new HBox(5);
        titleBox.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        titleBox.getChildren().addAll(titleLabel, region);

        Organization currentOrg = AppState.getInstance().getCurrentOrganization();
        if (currentOrg != null && organizationService.isCurrentUserOwner(currentOrg.getId())) {
            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> {
                try {
                    int projId = Integer.parseInt(viewName.split(":")[1]);
                    projectService.deleteProject(projId);
                    update();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            titleBox.getChildren().add(deleteButton);
        }

        card.getChildren().add(titleBox);

        if (description != null && !description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.getStyleClass().add("card-description");
            descLabel.setWrapText(true);
            card.getChildren().add(descLabel);
        }

        VBox progressBox = new VBox(5);
        HBox progressLabelBox = new HBox();
        Label progressLabel = new Label("Progress");
        progressLabel.getStyleClass().add("progress-label");
        Label progressValue = new Label((int) (progress * 100) + "%");
        progressValue.getStyleClass().add("progress-value");

        Region spacer = new Region();
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
        new CreateProjectModal(projectService, organizationService, AppState.getInstance().getCurrentOrganization(),
                this::update).show();
    }
}
