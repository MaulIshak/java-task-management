package com.taskmanager.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;

public class DashboardView extends VBox {

    public DashboardView() {
        setPadding(new Insets(30));
        setSpacing(30);
        getStyleClass().add("dashboard-view");




        // My Organizations Section
        VBox orgSection = createTitle("My Organizations");
        FlowPane orgContainer = new FlowPane();
        orgContainer.setHgap(20);
        orgContainer.setVgap(20);

        orgContainer.getChildren().addAll(
                createCard("Innovate Inc.", "3 members", 0.50),
                createCard("Creative Solutions", "2 members", 0.50));
        orgSection.getChildren().add(orgContainer);




        // My Projects Section
        VBox projectSection = createTitle("My Projects");
        FlowPane projectContainer = new FlowPane();
        projectContainer.setHgap(20);
        projectContainer.setVgap(20);
        projectContainer.getChildren().addAll(
                createCard("Phoenix Project", "A complete overhaul of the main customer-facing application.", "3 members", 0.40),
                createCard("Marketing Website", "Redesign and launch of the new company marketing website.", "2 members", 1.00),
                createCard("Mobile App", "Developing a new mobile application for iOS and Android.", "2 members", 0.50));
        projectSection.getChildren().add(projectContainer);




        // My Tasks Section
        VBox taskSection = createTitle("My Tasks");
        VBox taskList = new VBox(10);
        taskList.getChildren().addAll(
            createTaskItem("Implement user authentication", "Phoenix Project", "Feature", "11/29/2025")
        );
        taskSection.getChildren().add(taskList);

        getChildren().addAll(orgSection, projectSection, taskSection);
    }

    /**
     * Creates a titled section for the dashboard.
     */
    private VBox createTitle(String title) {
        VBox section = new VBox(15);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        section.getChildren().add(titleLabel);
        return section;
    }



    // Card for Organizations (Simple)
    private VBox createCard(String title, String members, double progress) {
        return createCard(title, null, members, progress);
    }



    // Card for Projects (With Description)
    private VBox createCard(String title, String description, String members, double progress) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(350);
        card.setMinWidth(300);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        card.getChildren().add(titleLabel);

        if (description != null) {
            Label descLabel = new Label(description);
            descLabel.getStyleClass().add("card-description");
            descLabel.setWrapText(true);
            card.getChildren().add(descLabel);
        }

        HBox membersBox = new HBox(5);
        // Placeholder for members count
        Label membersLabel = new Label(members);
        membersLabel.getStyleClass().add("card-members");
        membersBox.getChildren().add(membersLabel);
        card.getChildren().add(membersBox);

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

        // Checkbox placeholder (using a Button for now or just a region)
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
}
