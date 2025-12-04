package com.taskmanager.view;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import com.taskmanager.service.TaskService;
import com.taskmanager.util.UserSession;

public class MainLayout extends BorderPane implements Observer {

    private final ScrollPane contentArea;
    private Sidebar sidebar;
    private Navbar navbar;
    private VBox mainContent;

    // Services
    private final OrganizationService organizationService;
    private final ProjectService projectService;
    private final TaskService taskService;

    public MainLayout() {
        getStyleClass().add("main-layout");

        // Initialize Services
        this.organizationService = new OrganizationService();
        this.projectService = new ProjectService();
        this.taskService = new TaskService();

        // Content Area
        contentArea = new ScrollPane();
        contentArea.setFitToWidth(true);
        contentArea.getStyleClass().add("content-area");
        contentArea.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Register as observer
        UserSession.getInstance().registerObserver(this);
        

        // Initial check
        update();
    }

    @Override
    public void update() {
        if (UserSession.getInstance().isLoggedIn()) {
            setupMainLayout();

            // Default view if none selected
            if (contentArea.getContent() == null) {
                switchView("Dashboard");
            }
        } else {
            showLoginLayout();
        }
    }

    private void setupMainLayout() {
        if (sidebar == null) {
            sidebar = new Sidebar(this, organizationService, projectService);
        } else {
            sidebar.refresh(); // Refresh sidebar data on re-login or update
        }

        if (navbar == null) {
            navbar = new Navbar();
        }

        if (mainContent == null) {
            mainContent = new VBox();
            mainContent.getChildren().addAll(navbar, contentArea);
            VBox.setVgrow(contentArea, Priority.ALWAYS);
        }

        setLeft(sidebar);
        setCenter(mainContent);
    }

    private void showLoginLayout() {
        setLeft(null);
        setCenter(null);
        showLogin();
    }

    public void showLogin() {
        setCenter(new LoginView(this));
    }

    public void showRegister() {
        setCenter(new RegisterView(this));
    }

    public void switchView(String viewName) {
        if (viewName.startsWith("organization:")) {
            try {
                int orgId = Integer.parseInt(viewName.split(":")[1]);
                contentArea.setContent(new OrganizationDetailView(orgId, organizationService, projectService));
                navbar.setTitle("Organization Details");
            } catch (NumberFormatException e) {
                System.err.println("Invalid Organization ID: " + viewName);
            }
        } else if (viewName.startsWith("Project:")) {
            try {
                int projectId = Integer.parseInt(viewName.split(":")[1]);
                contentArea.setContent(new ProjectDetailView(projectId, projectService, taskService));
                navbar.setTitle("Project Details");
            } catch (NumberFormatException e) {
                System.err.println("Invalid Project ID: " + viewName);
            }
        } else {
            switch (viewName) {
                case "Dashboard":
                    contentArea.setContent(new DashboardView(organizationService, projectService, taskService));
                    if (navbar != null)
                        navbar.setTitle("Dashboard");
                    break;
                case "Settings":
                    // contentArea.setContent(new Label("Settings View"));
                    if (navbar != null)
                        navbar.setTitle("Settings");
                    break;
                default:
                    System.out.println("Unknown view: " + viewName);
            }
        }
    }

    private void showPlaceholder(String viewName) {
        Label placeholder = new Label("View: " + viewName);
        placeholder.setStyle("-fx-font-size: 24px; -fx-padding: 20;");
        contentArea.setContent(placeholder);
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public TaskService getTaskService() {
        return taskService;
    }
}
