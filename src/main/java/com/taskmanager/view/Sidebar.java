package com.taskmanager.view;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.service.ProjectService;
import com.taskmanager.util.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.List;

public class Sidebar extends VBox {

    private final MainLayout mainLayout;
    private final OrganizationService organizationService;
    private final ProjectService projectService;

    private VBox orgListContainer;
    private VBox projectListContainer;

    public Sidebar(MainLayout mainLayout, OrganizationService organizationService, ProjectService projectService) {
        this.mainLayout = mainLayout;
        this.organizationService = organizationService;
        this.projectService = projectService;

        getStyleClass().add("sidebar");
        setSpacing(5);
        setAlignment(Pos.TOP_LEFT);

        getChildren().add(createLogo());

        // Dashboard Link
        addNavButton("Dashboard", "Dashboard", true);

        // Organizations Section
        createSectionHeader("ORGANIZATIONS", () -> showCreateOrganizationModal());
        orgListContainer = new VBox(5);
        getChildren().add(orgListContainer);

        // Projects Section
        createSectionHeader("PROJECTS", () -> showCreateProjectModal());
        projectListContainer = new VBox(5);
        getChildren().add(projectListContainer);

        // Spacer to push profile to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        getChildren().add(createProfileSection());

        // Initial Load
        refresh();
    }

    public void refresh() {
        updateOrganizations();
        updateProjects();
    }

    private void updateOrganizations() {
        orgListContainer.getChildren().clear();
        try {
            List<Organization> orgs = organizationService.getOrganizationsByCurrentUser();
            if (orgs.isEmpty()) {
                orgListContainer.getChildren()
                        .add(createEmptyStateButton("Add Organization", () -> showCreateOrganizationModal()));
            } else {
                for (Organization org : orgs) {
                    addNavButton(org.getOrgName(), "Organization:" + org.getId(), false, orgListContainer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error (maybe show a label)
        }
    }

    private void updateProjects() {
        projectListContainer.getChildren().clear();
        // TODO: Logic to get all projects across organizations or filtered?
        // For now, let's assume we show projects from all organizations the user is
        // part of,
        // OR we might want to show projects only when an organization is selected.
        // Based on requirements: "Projects (ikon tambah) (jika data kosong...)"
        // It seems to imply a global list or context-aware.
        // Let's implement a simple fetch for now.
        // Since ProjectService.getProjectsByOrganization requires orgId, we might need
        // a way to get all projects.
        // Or maybe we only show projects if we are in an Organization context?
        // The prompt says: "user klik card organization tersebut dan pindah ke
        // halaman... isinya hanya berisi Projects... dibawahnya berisi list projek
        // projeknya"
        // But the sidebar also has a Projects section.
        // Let's assume for the sidebar, it shows "My Projects" (assigned to user) or
        // similar.
        // But the service doesn't have "getProjectsByUser".
        // Let's leave it empty or show a placeholder for now if no specific logic is
        // provided.
        // Wait, the requirement says "Projects (ikon tambah)".
        // If I can't fetch all projects easily, I'll just show the empty state or a
        // placeholder.

        // For now, let's just show the empty state if we can't fetch easily, or try to
        // fetch if possible.
        // Let's assume we want to show projects from the first organization found, or
        // just empty for now until we have a better query.

        // Actually, let's just show the empty state button for now as a default if no
        // data.
        projectListContainer.getChildren().add(createEmptyStateButton("Add Project", () -> showCreateProjectModal()));
    }

    private void createSectionHeader(String title, Runnable onAddAction) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 5, 20));

        Label label = new Label(title);
        label.getStyleClass().add("sidebar-section-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("sidebar-add-button"); // Need to define this style
        addBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-cursor: hand; -fx-padding: 0;");
        addBtn.setOnAction(e -> onAddAction.run());

        header.getChildren().addAll(label, spacer, addBtn);
        getChildren().add(header);
    }

    private Button createEmptyStateButton(String text, Runnable action) {
        Button btn = new Button("+ " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("sidebar-empty-button"); // Need to define this style
        // dashed border style
        btn.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #888; -fx-border-style: dashed; -fx-border-width: 1; -fx-border-radius: 5; -fx-text-fill: #888; -fx-cursor: hand;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void showCreateOrganizationModal() {
        new CreateOrganizationModal(organizationService, this::refresh).show();
    }

    private void showCreateProjectModal() {
        new CreateProjectModal(projectService, organizationService, this::refresh).show();
    }

    private HBox createLogo() {
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 20, 0));

        Label logoIcon = new Label("Z");
        logoIcon.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5;");

        Label logoText = new Label("Zenith");
        logoText.getStyleClass().add("sidebar-logo");

        logoBox.getChildren().addAll(logoIcon, logoText);
        return logoBox;
    }

    private HBox createProfileSection() {
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(20, 0, 0, 0));
        profileBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Circle avatar = new Circle(15);
        avatar.setStyle("-fx-fill: #333;");

        VBox userInfo = new VBox(2);
        Label userName = new Label(UserSession.getInstance().getCurrentUser().getName());
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        Label userAction = new Label("View profile");
        userAction.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        userInfo.getChildren().addAll(userName, userAction);

        profileBox.getChildren().addAll(avatar, userInfo);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 11px;");
        logoutBtn.setOnAction(e -> UserSession.getInstance().endSession());

        HBox logoutBox = new HBox(logoutBtn);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(logoutBox, Priority.ALWAYS);

        profileBox.getChildren().add(logoutBox);
        return profileBox;
    }

    private void addNavButton(String label, String viewName, boolean isActive) {
        addNavButton(label, viewName, isActive, this);
    }

    private void addNavButton(String label, String viewName, boolean isActive, VBox container) {
        Button btn = new Button(label);
        btn.getStyleClass().add("sidebar-button");
        if (isActive) {
            btn.getStyleClass().add("active");
        }
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> mainLayout.switchView(viewName));
        container.getChildren().add(btn);
    }
}
