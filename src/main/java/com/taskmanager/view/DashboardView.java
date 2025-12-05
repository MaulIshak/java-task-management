package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import com.taskmanager.service.TaskService;
import com.taskmanager.util.UserSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class DashboardView extends VBox implements Observer, View {

    private final OrganizationService organizationService;
    private final ProjectService projectService;
    private final TaskService taskService;

    private FlowPane orgContainer;
    private FlowPane projectContainer;
    private VBox taskList;

    public DashboardView(OrganizationService organizationService, ProjectService projectService,
            TaskService taskService) {
        this.organizationService = organizationService;
        this.projectService = projectService;
        this.taskService = taskService;

        organizationService.registerObserver(this);
        projectService.registerObserver(this);
        taskService.registerObserver(this);
        AppState.getInstance().registerObserver(this);
        UserSession.getInstance().registerObserver(this);

        render();
    }

    @Override
    public void render() {
        getChildren().clear();

        if (!UserSession.getInstance().isLoggedIn()) {
            return;
        }

        setPadding(new Insets(30));
        setSpacing(30);
        getStyleClass().add("dashboard-view");

        VBox orgSection = createSectionHeader("My Organizations", true);
        orgContainer = createFlowPane();
        orgSection.getChildren().add(orgContainer);

        VBox projectSection = createSectionHeader("My Projects", false);
        projectContainer = createFlowPane();
        projectSection.getChildren().add(projectContainer);

        VBox taskSection = createSectionHeader("My Tasks", false);
        taskList = new VBox(10);
        taskSection.getChildren().add(taskList);

        getChildren().addAll(orgSection, projectSection, taskSection);

        refreshData();
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            if (UserSession.getInstance().isLoggedIn()) {
                if (getChildren().isEmpty()) {
                    render();
                } else {
                    refreshData();
                }
            } else {
                getChildren().clear();
            }
        });
    }

    private void refreshData() {
        if (!UserSession.getInstance().isLoggedIn())
            return;
        loadOrganizations();
        loadProjects();
        loadTasks();
    }

    private FlowPane createFlowPane() {
        FlowPane pane = new FlowPane();
        pane.setHgap(20);
        pane.setVgap(20);
        return pane;
    }

    private void loadOrganizations() {
        orgContainer.getChildren().clear();
        try {
            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();
            if (orgs.isEmpty()) {
                orgContainer.getChildren().add(new Label("No organizations yet."));
                return;
            }

            orgs.forEach(org -> {
                double totalProgress = 0;
                int projectCount = 0;
                List<Project> projects = projectService.getProjectsByOrganization(org.getId());
                for (Project p : projects) {
                    List<Task> tasks = taskService.getTasksByProject(p.getId());
                    if (!tasks.isEmpty()) {
                        long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
                        totalProgress += (double) doneCount / tasks.size();
                    }
                    projectCount++;
                }
                double avgProgress = projectCount > 0 ? totalProgress / projectCount : 0.0;

                VBox card = createGenericCard(org.getOrgName(), null, org.getMembers().size() + " Members", avgProgress,
                        "organization:" + org.getId());
                orgContainer.getChildren().add(card);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProjects() {
        projectContainer.getChildren().clear();

        try {
            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();
            if (orgs.isEmpty()) {
                projectContainer.getChildren().add(new Label("No projects yet."));
                return;
            }

            orgs.forEach(org -> {
                List<Project> projects = projectService.getProjectsByOrganization(org.getId());

                projects.forEach(project -> {
                    // Calculate progress
                    List<Task> tasks = taskService.getTasksByProject(project.getId());
                    double progress = 0.0;
                    if (!tasks.isEmpty()) {
                        long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
                        progress = (double) doneCount / tasks.size();
                    }

                    VBox card = createGenericCard(project.getName(), project.getDescription(), "Members: N/A", progress,
                            "Project:" + project.getId());
                    projectContainer.getChildren().add(card);
                });

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        taskList.getChildren().clear();

        try {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser == null)
                return;

            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();

            boolean hasTasks = false;

            for (Organization org : orgs) {
                List<Project> projects = projectService.getProjectsByOrganization(org.getId());
                for (Project proj : projects) {
                    // Fetch tasks from service
                    List<Task> tasks = taskService.getTasksByProject(proj.getId());
                    for (Task task : tasks) {
                        if (task.getAssignee() != null && task.getAssignee().getId() == currentUser.getId()) {
                            taskList.getChildren().add(createTaskItem(
                                    task.getTitle(),
                                    proj.getName(),
                                    task.getStatus().toString(),
                                    task.getDueDate() != null ? task.getDueDate().toString() : "No Date",
                                    proj.getId())); // Passing project ID
                            hasTasks = true;
                        }
                    }
                }
            }

            if (!hasTasks) {
                taskList.getChildren().add(new Label("No tasks assigned to you."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            taskList.getChildren().add(new Label("Error loading tasks."));
        }
    }

    private VBox createSectionHeader(String title, boolean actions) {
        VBox section = new VBox(15);
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        header.getChildren().add(titleLabel);

        if (actions) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            header.getChildren().add(spacer);

            Button join = new Button("Join Organization");
            join.setStyle(
                    "-fx-background-color: white; -fx-text-fill: #007bff; -fx-border-color: #007bff; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;");
            join.setOnAction(e -> new JoinOrganizationModal(organizationService, this::refreshData).show());
            header.getChildren().add(join);

            Button add = new Button("+ Add Organization");
            add.getStyleClass().add("primary-button");
            add.setOnAction(e -> new CreateOrganizationModal(organizationService, this::refreshData).show());
            header.getChildren().add(add);
        }

        section.getChildren().add(header);
        return section;
    }

    private VBox createGenericCard(String title, String description, String metaInfo, double progress,
            String viewName) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(350);
        card.setMinWidth(300);

        if (viewName != null) {
            card.setStyle(card.getStyle() + "-fx-cursor: hand;");
            card.setOnMouseClicked(e -> {
                try {
                    if (viewName.startsWith("organization:")) {
                        int orgId = Integer.parseInt(viewName.split(":")[1]);
                        Organization org = organizationService.getOrganizationDetails(orgId);
                        AppState.getInstance().setCurrentOrganization(org);
                        AppState.getInstance().setCurrentProject(null);
                        AppState.getInstance().switchView(ViewName.PROJECTS);
                    } else if (viewName.startsWith("Project:")) {
                        int projId = Integer.parseInt(viewName.split(":")[1]);
                        Project proj = projectService.getProjectWithTasks(projId);
                        AppState.getInstance().setCurrentProject(proj);
                        AppState.getInstance().switchView(ViewName.PROJECT_DETAIL);
                    } else {
                        ViewName vn = ViewName.valueOf(viewName);
                        AppState.getInstance().switchView(vn);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Navigation failed for: " + viewName);
                }
            });
        }

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

    private HBox createTaskItem(String title, String project, String tag, String date, int projectId) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(15));
        row.getStyleClass().add("dashboard-task-item"); // New class for styling
        row.setAlignment(Pos.CENTER_LEFT);

        // Make clickable
        row.setStyle("-fx-cursor: hand;");
        row.setOnMouseClicked(e -> {
            try {
                Project proj = projectService.getProjectWithTasks(projectId);
                AppState.getInstance().setCurrentProject(proj);
                AppState.getInstance().switchView(ViewName.PROJECT_DETAIL);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox titleBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("task-item-title");
        Label projectLabel = new Label(project);
        projectLabel.getStyleClass().add("task-item-project");
        titleBox.getChildren().addAll(titleLabel, projectLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label tagLabel = new Label(tag);
        tagLabel.getStyleClass().add("task-item-status");

        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("task-item-date");

        row.getChildren().addAll(titleBox, spacer, tagLabel, dateLabel);
        return row;
    }
}
