package com.taskmanager.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.util.UserSession;

public class MainLayout extends BorderPane implements Observer {

    private final ScrollPane contentArea;
    private Sidebar sidebar;
    private Navbar navbar;

    public MainLayout() {
        getStyleClass().add("main-layout");

        // Content Area
        contentArea = new ScrollPane();
        contentArea.setFitToWidth(true);
        contentArea.getStyleClass().add("content-area");

        // Register as observer
        UserSession.getInstance().registerObserver(this);

        // Initial check
        update();
    }

    @Override
    public void update() {
        if (UserSession.getInstance().isLoggedIn()) {
            if (sidebar == null) {
                sidebar = new Sidebar(this);
            }
            if (navbar == null) {
                navbar = new Navbar(this);
            }
            setLeft(sidebar);
            setTop(navbar);
            setCenter(contentArea);

            // Only switch to Intro if content is empty or we just logged in
            if (contentArea.getContent() == null) {
                switchView("Intro");
            }
        } else {
            setLeft(null);
            setTop(null);
            showLogin();
        }
    }

    public void showLogin() {
        setCenter(new LoginView(this));
    }

    public void showRegister() {
        setCenter(new RegisterView(this));
    }

    public void switchView(String viewName) {
        Node view;
        switch (viewName) {
            case "Layouts":
                // view = new LayoutDemoView(); // Assuming this exists or will be created
                view = null; // Placeholder
                break;
            case "Intro":
            default:
                // view = new DocumentationView(...); // Placeholder
                view = null;
                break;
        }
        if (view != null) {
            contentArea.setContent(view);
        }
    }
}
