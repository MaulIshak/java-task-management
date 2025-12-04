package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import com.taskmanager.service.TaskService;
import com.taskmanager.util.UserSession;
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

import java.util.List;

public class DashboardView extends VBox {

    private final OrganizationService organizationService;
    private final ProjectService projectService;
    private final TaskService taskService;

    public DashboardView(OrganizationService organizationService, ProjectService projectService,
            TaskService taskService) {
        this.organizationService = organizationService;
        this.projectService = projectService;
        this.taskService = taskService;

        setPadding(new Insets(30));
        setSpacing(30);
        getStyleClass().add("dashboard-view");

        // My Organizations Section
        VBox orgSection = createSectionHeader("My Organizations", true);
        FlowPane orgContainer = new FlowPane();
        orgContainer.setHgap(20);
        orgContainer.setVgap(20);
        loadOrganizations(orgContainer);
        orgSection.getChildren().add(orgContainer);

        // My Projects Section
        VBox projectSection = createSectionHeader("My Projects", false);
        FlowPane projectContainer = new FlowPane();
        projectContainer.setHgap(20);
        projectContainer.setVgap(20);
        loadProjects(projectContainer);
        projectSection.getChildren().add(projectContainer);

        // My Tasks Section
        VBox taskSection = createSectionHeader("My Tasks", false);
        VBox taskList = new VBox(10);
        loadTasks(taskList);
        taskSection.getChildren().add(taskList);

        getChildren().addAll(orgSection, projectSection, taskSection);
    }

    private void loadOrganizations(FlowPane container) {
        try {
            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();
            if (orgs.isEmpty()) {
                // Show empty state or nothing? Requirement says "jika data kosong terdapat
                // sebuah button border dashed..."
                // But that was for Sidebar. For Dashboard it says "data organsiasi berhasil di
                // tambah muncul di dashboard".
                // Let's show a placeholder if empty.
                Label emptyLabel = new Label("No organizations found. Join or create one!");
                container.getChildren().add(emptyLabel);
            } else {
                for (Organization org : orgs) {
                    // Calculate progress or member count if available
                    String members = (org.getMembers() != null ? org.getMembers().size() : 0) + " members";
                    // Progress is not really applicable to Org unless we aggregate projects. Let's
                    // use 0 for now.
                    container.getChildren().add(
                            createGenericCard(org.getOrgName(), null, members, 0.0, "organization:" + org.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProjects(FlowPane container) {
        // TODO: Load projects. Since we don't have getProjectsByUser, we might need to
        // iterate orgs.
        // For now, placeholder or empty.
        // Let's try to get projects from the first few organizations.
        try {
            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();
            boolean foundAny = false;
            for (Organization org : orgs) {
                List<Project> projects = projectService.getProjectsByOrganization(org.getId());
                for (Project proj : projects) {
                    foundAny = true;
                    // Description is available in Project model
                    container.getChildren()
                            .add(createGenericCard(proj.getName(), proj.getDescription(), "Members: N/A", 0.0,
                                    "Project:" + proj.getId()));
                }
            }
            if (!foundAny) {
                Label emptyLabel = new Label("No projects found.");
                container.getChildren().add(emptyLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTasks(VBox container) {
        // Placeholder for tasks
        container.getChildren()
                .add(createTaskItem("Implement user authentication", "Phoenix Project", "Feature", "11/29/2025"));
    }

    /**
     * Creates a titled section for the dashboard.
     */
    private VBox createSectionHeader(String title, boolean showActions) {
        VBox section = new VBox(15);
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        header.getChildren().add(titleLabel);

        if (showActions) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            header.getChildren().add(spacer);

            Button addBtn = new Button("+ Add");
            addBtn.setOnAction(e -> showCreateOrganizationModal());

            Button joinBtn = new Button("Join");
            joinBtn.setOnAction(e -> showJoinOrganizationModal());

            header.getChildren().addAll(addBtn, joinBtn);
        }

        section.getChildren().add(header);
        return section;
    }

    // Generic Section Card
    private VBox createGenericCard(String title, String description, String metaInfo, double progress,
            String viewName) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(350);
        card.setMinWidth(300);

        if (viewName != null) {
            card.setStyle(card.getStyle() + "-fx-cursor: hand;");
            card.setOnMouseClicked(e -> {
                // We need to switch view.
                // Since we don't have direct access to MainLayout's switchView, we can fire an
                // event or use a static helper.
                // But MainLayout is the parent.
                // Let's try to find MainLayout from scene.
                if (getScene() != null && getScene().getRoot() instanceof javafx.scene.layout.BorderPane) {
                    // MainLayout extends BorderPane? No, it extends StackPane or similar?
                    // Let's check MainLayout. It extends BorderPane.
                    // But we can't cast easily without circular dependency or public method.
                    // Actually MainLayout is in same package.
                    if (getScene().getRoot() instanceof MainLayout) {
                        ((MainLayout) getScene().getRoot()).switchView(viewName);
                    } else {
                        // Fallback or debug
                        System.out.println("Navigate to: " + viewName);
                        // Try to find MainLayout in parent hierarchy if it's not root
                        javafx.scene.Parent parent = getParent();
                        while (parent != null) {
                            if (parent instanceof MainLayout) {
                                ((MainLayout) parent).switchView(viewName);
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
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

    private HBox createTaskItem(String title, String project, String tag, String date) {
        HBox item = new HBox(15);
        item.getStyleClass().add("task-item");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));

        Button checkbox = new Button();
        checkbox.getStyleClass().add("task-checkbox");

        VBox content = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("task-title");
        Label projectLabel = new Label(project);
        projectLabel.getStyleClass().add("task-project");
        content.getChildren().addAll(titleLabel, projectLabel);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label tagLabel = new Label(tag);
        tagLabel.getStyleClass().add("task-tag");

        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("task-date");

        item.getChildren().addAll(checkbox, content, spacer, tagLabel, dateLabel);
        return item;
    }

    private void showCreateOrganizationModal() {
        new CreateOrganizationModal(organizationService, () -> {
            // Refresh dashboard
            // Ideally we should have a refresh method here too, or re-instantiate the view.
            // For now, let's just reload the org section if possible, but DashboardView
            // doesn't have a clear method.
            // Let's reload the whole view via MainLayout if possible, but we don't have
            // reference to MainLayout here.
            // Wait, DashboardView is created by MainLayout.
            // Maybe we should pass a refresh callback or just refresh the specific
            // container?
            // DashboardView extends VBox, so we can clear and rebuild, but constructor does
            // the building.
            // Let's add a refresh method to DashboardView.
            refresh();
        }).show();
    }

    private void showJoinOrganizationModal() {
        new JoinOrganizationModal(organizationService, this::refresh).show();
    }

    public void refresh() {
        getChildren().clear();
        // Re-run constructor logic
        // My Organizations Section
        VBox orgSection = createSectionHeader("My Organizations", true);
        FlowPane orgContainer = new FlowPane();
        orgContainer.setHgap(20);
        orgContainer.setVgap(20);
        loadOrganizations(orgContainer);
        orgSection.getChildren().add(orgContainer);

        // My Projects Section
        VBox projectSection = createSectionHeader("My Projects", false);
        FlowPane projectContainer = new FlowPane();
        projectContainer.setHgap(20);
        projectContainer.setVgap(20);
        loadProjects(projectContainer);
        projectSection.getChildren().add(projectContainer);

        // My Tasks Section
        VBox taskSection = createSectionHeader("My Tasks", false);
        VBox taskList = new VBox(10);
        loadTasks(taskList);
        taskSection.getChildren().add(taskList);

        getChildren().addAll(orgSection, projectSection, taskSection);
    }
}
