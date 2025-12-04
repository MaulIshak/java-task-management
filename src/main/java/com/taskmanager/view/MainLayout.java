package com.taskmanager.view;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.util.UserSession;

/**
 * The main layout of the application.
 * <p>
 * This class acts as the central hub for the application's UI flow.
 * It manages the navigation between different views (Dashboard, Login,
 * Register, etc.)
 * and handles the display of the Sidebar and Navbar based on the user's login
 * state.
 * </p>
 */
public class MainLayout extends BorderPane implements Observer {

    private final ScrollPane contentArea;
    private Sidebar sidebar;
    private Navbar navbar;
    private VBox mainContent;

    public MainLayout() {
        getStyleClass().add("main-layout");

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

    /**
     * Updates the layout based on the user's session state.
     * If logged in, shows the main application UI (Sidebar, Navbar, Content).
     * If not logged in, shows the Login view.
     */
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
            sidebar = new Sidebar(this);
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

    /**
     * Switches the content area to the specified view.
     * 
     * @param viewName The name of the view to switch to.
     */
    public void switchView(String viewName) {
        Node view = createView(viewName);

        if (view != null) {
            contentArea.setContent(view);
            if (navbar != null) {
                navbar.setTitle(viewName);
            }
        } else {
            showPlaceholder(viewName);
        }
    }

    private Node createView(String viewName) {
        switch (viewName) {
            case "Dashboard":
                return new DashboardView();
            default:
                return null;
        }
    }

    private void showPlaceholder(String viewName) {
        Label placeholder = new Label("View: " + viewName);
        placeholder.setStyle("-fx-font-size: 24px; -fx-padding: 20;");
        contentArea.setContent(placeholder);
    }
}
