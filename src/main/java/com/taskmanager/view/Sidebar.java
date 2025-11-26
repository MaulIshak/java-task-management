package com.taskmanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    private final MainLayout mainLayout;

    public Sidebar(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        getStyleClass().add("sidebar");
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);

        addNavButton("Introduction", "Intro");
        addNavButton("Layouts (Flexbox)", "Layouts");
        addNavButton("Positioning", "Positioning");
        addNavButton("Drag & Drop", "DragDrop");
        addNavButton("CSS Selectors", "CssSelectors");
        addNavButton("Traversal (JS-like)", "Traversal");
        addNavButton("Advanced Styling", "AdvancedStyling");
        addNavButton("Data Controls", "DataControls");
        addNavButton("Animations", "Animations");
        addNavButton("Components", "Components");
        addNavButton("Scrolling", "Scrolling");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("sidebar-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> com.taskmanager.util.UserSession.getInstance().endSession());
        getChildren().add(logoutBtn);
    }

    private void addNavButton(String label, String viewName) {
        Button btn = new Button(label);
        btn.getStyleClass().add("sidebar-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> mainLayout.switchView(viewName));
        getChildren().add(btn);
    }
}
