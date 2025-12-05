package com.taskmanager.view;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.util.UserSession;

public class MainLayout extends BorderPane implements Observer {
    private static MainLayout instance;

    private ScrollPane contentArea;
    private Sidebar sidebar;
    private Navbar navbar;
    private VBox mainContent;

    public static synchronized MainLayout getInstance() {
        if (instance == null) {
            instance = new MainLayout();
        }
        return instance;
    }

    private MainLayout() {
        getStyleClass().add("main-layout");

        contentArea = new ScrollPane();
        contentArea.setFitToWidth(true);
        contentArea.setStyle("-fx-background-color: transparent;");
        contentArea.getStyleClass().add("content-area");

        UserSession.getInstance().registerObserver(this);
        AppState.getInstance().registerObserver(this);
    }

    @Override
    public void update() {
        if (UserSession.getInstance().isLoggedIn()) {
            setupMainLayout();
            ViewName targetView = AppState.getInstance().getCurrentView();
            if (targetView != null) {
                switchView(targetView);
            } else if (contentArea.getContent() == null) {
                AppState.getInstance().switchView(ViewName.DASHBOARD);
            }
        } else {
            showLoginLayout();
        }
    }

    // ------------------ SETUP MAIN UI STRUCTURE ------------------
    private void setupMainLayout() {
        if (sidebar == null)
            sidebar = ViewFactory.getInstance().createSidebar();
        if (navbar == null)
            navbar = new Navbar();
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

    // ------------------ LOGIN & REGISTER ------------------
    public void showLogin() {
        setCenter(ViewFactory.getInstance().getView(ViewName.LOGIN));
    }

    public void showRegister() {
        setCenter(ViewFactory.getInstance().getView(ViewName.REGISTER));
    }

    // ------------------ VIEW SWITCHER ------------------
    public void switchView(ViewName viewName) {
        Node viewContent = ViewFactory.getInstance().getView(viewName);
        contentArea.setContent(viewContent);
        if (sidebar != null) {
            sidebar.setActive(viewName);
        }
    }
}
