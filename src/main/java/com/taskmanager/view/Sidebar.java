package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.model.User;
import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import com.taskmanager.util.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sidebar extends VBox implements Observer {
    Label name;
    private VBox orgListContainer;
    private VBox projectListContainer;

    private static final String SIDEBAR_BUTTON_STYLE = "sidebar-button";
    private static final String ACTIVE_STYLE = "active";

    private final Map<ViewName, Button> navButtons = new EnumMap<>(ViewName.class);

    private final OrganizationService organizationService;
    private final ProjectService projectService;

    public Sidebar(OrganizationService organizationService, ProjectService projectService) {
        this.organizationService = organizationService;
        this.projectService = projectService;

        name = new Label();
        name.getStyleClass().add("sidebar-profile-name");

        getStyleClass().add("sidebar");
        setSpacing(12);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_LEFT);

        this.organizationService.registerObserver(this);
        this.projectService.registerObserver(this);
        UserSession.getInstance().registerObserver(this);
        AppState.getInstance().registerObserver(this);

        setupLayout();
        update();
    }

    private void setupLayout() {
        getChildren().add(createLogo());
        addNavButton("Dashboard", ViewName.DASHBOARD);

        createSectionHeader("ORGANIZATIONS", this::showCreateOrganizationModal);
        orgListContainer = new VBox(4);
        getChildren().add(orgListContainer);

        createSectionHeader("PROJECTS", this::showCreateProjectModal);
        projectListContainer = new VBox(4);
        getChildren().add(projectListContainer);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        getChildren().add(createProfileSection());
    }

    private final Map<Integer, Button> orgButtons = new HashMap<>();
    private final Map<Integer, Button> projectButtons = new HashMap<>();

    @Override
    public void update() {
        if (UserSession.getInstance().isLoggedIn()) {
            if (UserSession.getInstance().getCurrentUser() != null) {
                name.setText(UserSession.getInstance().getCurrentUser().getName());
            }
            updateOrganizations();
            updateProjects();

            ViewName currentView = AppState.getInstance().getCurrentView();
            if (currentView != null) {
                setActive(currentView);
            }
        } else {
            // Clear data on logout
            name.setText("");
            orgListContainer.getChildren().clear();
            projectListContainer.getChildren().clear();
            orgButtons.clear();
            projectButtons.clear();
        }
    }

    private void updateOrganizations() {
        orgListContainer.getChildren().clear();
        orgButtons.clear();
        List<Organization> list;
        try {
            list = organizationService.getOrganizationsByCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (list.isEmpty()) {
            orgListContainer.getChildren()
                    .add(createEmptyStateButton("Add Organization", this::showCreateOrganizationModal));
            return;
        }

        list.forEach(org -> {
            Button btn = new Button(org.getOrgName());
            btn.getStyleClass().add(SIDEBAR_BUTTON_STYLE);
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(e -> {
                AppState.getInstance().setCurrentOrganization(org);
                AppState.getInstance().setCurrentProject(null);
                AppState.getInstance().switchView(ViewName.PROJECTS);
            });

            orgButtons.put(org.getId(), btn);
            orgListContainer.getChildren().add(btn);
        });
    }

    private void updateProjects() {
        projectListContainer.getChildren().clear();
        projectButtons.clear();
        List<Project> list = new ArrayList<>();
        try {
            if (AppState.getInstance().getCurrentOrganization() != null) {
                list = projectService
                        .getProjectsByOrganization(AppState.getInstance().getCurrentOrganization().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (list.isEmpty()) {
            if (AppState.getInstance().getCurrentOrganization() != null &&
                    organizationService.isCurrentUserOwner(AppState.getInstance().getCurrentOrganization().getId())) {
                projectListContainer.getChildren()
                        .add(createEmptyStateButton("Add Project", this::showCreateProjectModal));
            } else {
                Label emptyLabel = new Label("No projects found.");
                emptyLabel.getStyleClass().add("sidebar-label");
                projectListContainer.getChildren().add(emptyLabel);
            }
            return;
        }

        list.forEach(project -> {
            Button btn = new Button(project.getName());
            btn.getStyleClass().add(SIDEBAR_BUTTON_STYLE);
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(e -> {
                AppState.getInstance().setCurrentOrganization(AppState.getInstance().getCurrentOrganization());
                AppState.getInstance().setCurrentProject(project);
                AppState.getInstance().switchView(ViewName.PROJECT_DETAIL);
            });

            projectButtons.put(project.getId(), btn);
            projectListContainer.getChildren().add(btn);
        });
    }

    private void createSectionHeader(String title, Runnable onAddAction) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(8, 0, 4, 0));

        Label label = new Label(title);
        label.getStyleClass().add("sidebar-section-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(label, spacer);

        if (onAddAction != null) {
            // Logic khusus untuk header PROJECTS
            if (title.equals("PROJECTS")) {
                if (AppState.getInstance().getCurrentOrganization() != null &&
                        organizationService
                                .isCurrentUserOwner(AppState.getInstance().getCurrentOrganization().getId())) {
                    Button addBtn = new Button("+");
                    addBtn.getStyleClass().add("sidebar-add-button");
                    addBtn.setOnAction(e -> onAddAction.run());
                    header.getChildren().add(addBtn);
                }
            } else {
                // Untuk ORGANIZATIONS selalu tampil (atau sesuaikan logic jika perlu)
                Button addBtn = new Button("+");
                addBtn.getStyleClass().add("sidebar-add-button");
                addBtn.setOnAction(e -> onAddAction.run());
                header.getChildren().add(addBtn);
            }
        }
        getChildren().add(header);
    }

    private Button createEmptyStateButton(String text, Runnable action) {
        Button btn = new Button("+ " + text);
        btn.getStyleClass().add("sidebar-empty-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private HBox createLogo() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("Z");
        icon.getStyleClass().add("sidebar-logo-icon");

        Label text = new Label("Zenith");
        text.getStyleClass().add("sidebar-logo");

        box.getChildren().addAll(icon, text);
        return box;
    }

    private HBox createProfileSection() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("sidebar-profile-section");

        Circle avatar = new Circle(15);
        avatar.setStyle("-fx-fill:#333;");

        Button logout = new Button("Logout");
        logout.getStyleClass().add("sidebar-logout-button");
        logout.setOnAction(e -> {
            AppState.getInstance().logout();
            UserSession.getInstance().endSession();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        box.getChildren().addAll(avatar, name, spacer, logout);
        return box;
    }

    private Button addNavButton(String label, ViewName viewName) {
        Button btn = new Button(label);
        btn.getStyleClass().add(SIDEBAR_BUTTON_STYLE);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> AppState.getInstance().switchView(viewName));

        navButtons.put(viewName, btn);
        getChildren().add(btn);
        return btn;
    }

    public void setActive(ViewName name) {
        // Reset all active states
        navButtons.values().forEach(b -> b.getStyleClass().remove(ACTIVE_STYLE));
        orgButtons.values().forEach(b -> b.getStyleClass().remove(ACTIVE_STYLE));
        projectButtons.values().forEach(b -> b.getStyleClass().remove(ACTIVE_STYLE));

        if (navButtons.containsKey(name)) {
            navButtons.get(name).getStyleClass().add(ACTIVE_STYLE);
        } else if (name == ViewName.PROJECTS) {
            Organization currentOrg = AppState.getInstance().getCurrentOrganization();
            if (currentOrg != null && orgButtons.containsKey(currentOrg.getId())) {
                orgButtons.get(currentOrg.getId()).getStyleClass().add(ACTIVE_STYLE);
            }
        } else if (name == ViewName.PROJECT_DETAIL) {
            Project currentProject = AppState.getInstance().getCurrentProject();
            if (currentProject != null && projectButtons.containsKey(currentProject.getId())) {
                projectButtons.get(currentProject.getId()).getStyleClass().add(ACTIVE_STYLE);
            }
            // Also highlight the organization
            Organization currentOrg = AppState.getInstance().getCurrentOrganization();
            if (currentOrg != null && orgButtons.containsKey(currentOrg.getId())) {
                orgButtons.get(currentOrg.getId()).getStyleClass().add(ACTIVE_STYLE);
            }
        }
    }

    private void showCreateOrganizationModal() {
        new CreateOrganizationModal(organizationService, this::update).show();
    }

    private void showCreateProjectModal() {
        new CreateProjectModal(projectService, organizationService, this::update).show();
    }
}
