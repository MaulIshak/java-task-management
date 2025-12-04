package com.taskmanager.view;

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

public class Sidebar extends VBox {

    private final MainLayout mainLayout;

    public Sidebar(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        getStyleClass().add("sidebar");
        setSpacing(5);
        setAlignment(Pos.TOP_LEFT);
        

        getChildren().add(createLogo());

        // Dashboard Link
        addNavButton("Dashboard", "Dashboard", true);

        // Organizations Section
        addSectionLabel("ORGANIZATIONS");
        addNavButton("Innovate Inc.", "Org1", false); // Dummy links
        addNavButton("Creative Solutions", "Org2", false);

        // Projects Section
        addSectionLabel("PROJECTS");
        addNavButton("Phoenix Project", "Project1", false);
        addNavButton("Marketing Website", "Project2", false);

        // Spacer to push profile to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        getChildren().add(createProfileSection());
    }

    private HBox createLogo() {
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 20, 0));

        // Placeholder for Logo Icon (Blue Square with Z)
        Label logoIcon = new Label("Z");
        logoIcon.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5;");

        Label logoText = new Label("Zenith");
        logoText.getStyleClass().add("sidebar-logo");
        logoText.setPadding(new Insets(0)); // Reset padding from style class if needed

        logoBox.getChildren().addAll(logoIcon, logoText);
        return logoBox;
    }

    private HBox createProfileSection() {
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setPadding(new Insets(20, 0, 0, 0));
        profileBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        // Avatar placeholder
        Circle avatar = new Circle(15);
        avatar.setStyle("-fx-fill: #333;");


        VBox userInfo = new VBox(2);
        Label userName = new Label(UserSession.getInstance().getCurrentUser().getName()); 
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        Label userAction = new Label("View profile");
        userAction.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        userInfo.getChildren().addAll(userName, userAction);

        profileBox.getChildren().addAll(avatar, userInfo);

        // Logout functionality on profile click for now, or add a separate button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 11px;");
        logoutBtn.setOnAction(e -> UserSession.getInstance().endSession());

        HBox logoutBox = new HBox(logoutBtn);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(logoutBox, Priority.ALWAYS);

        profileBox.getChildren().add(logoutBox);
        return profileBox;
    }

    private void addSectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("sidebar-section-label");
        getChildren().add(label);
    }

    private void addNavButton(String label, String viewName, boolean isActive) {
        Button btn = new Button(label);
        btn.getStyleClass().add("sidebar-button");
        if (isActive) {
            btn.getStyleClass().add("active");
        }
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> mainLayout.switchView(viewName));
        getChildren().add(btn);
    }
}
