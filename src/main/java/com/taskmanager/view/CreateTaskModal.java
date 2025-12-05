package com.taskmanager.view;

import com.taskmanager.service.TaskService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;

public class CreateTaskModal {

    private final TaskService taskService;
    private final int projectId;
    private final Runnable onSuccess;

    public CreateTaskModal(TaskService taskService, int projectId, Runnable onSuccess) {
        this.taskService = taskService;
        this.projectId = projectId;
        this.onSuccess = onSuccess;
    }

    public void show() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        root.setOnMouseClicked(e -> {
            if (e.getTarget() == root) {
                dialog.close();
            }
        });

        VBox dialogVbox = new VBox(15);
        dialogVbox.setMaxSize(500, 600);
        dialogVbox.setAlignment(Pos.TOP_LEFT);
        dialogVbox.setPadding(new Insets(30));
        dialogVbox.setStyle(
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-background-radius: 10;");

        Label titleLabel = new Label("Create Task");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Title Input
        VBox titleGroup = new VBox(5);
        Label taskTitleLabel = new Label("Title");
        taskTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        titleField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        titleGroup.getChildren().addAll(taskTitleLabel, titleField);

        // Description Input
        VBox descGroup = new VBox(5);
        Label descLabel = new Label("Description");
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Task description...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        descGroup.getChildren().addAll(descLabel, descArea);

        // Due Date & Status
        HBox row1 = new HBox(20);

        VBox dateGroup = new VBox(5);
        Label dateLabel = new Label("Due Date");
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        DatePicker datePicker = new DatePicker();
        dateGroup.getChildren().addAll(dateLabel, datePicker);

        VBox statusGroup = new VBox(5);
        Label statusLabel = new Label("Status");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("TODO", "ON_PROGRESS", "DONE");
        statusBox.setValue("TODO");
        statusGroup.getChildren().addAll(statusLabel, statusBox);

        row1.getChildren().addAll(dateGroup, statusGroup);

        // Tag & Assignee
        HBox row2 = new HBox(20);

        VBox tagGroup = new VBox(5);
        Label tagLabel = new Label("Tag");
        tagLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        ComboBox<String> tagBox = new ComboBox<>();
        tagBox.getItems().addAll("Feature", "Bug", "Design", "Refactor");
        tagBox.setValue("Feature");
        tagGroup.getChildren().addAll(tagLabel, tagBox);

        VBox assigneeGroup = new VBox(5);
        Label assigneeLabel = new Label("Assignee");
        assigneeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        ComboBox<String> assigneeBox = new ComboBox<>();

        assigneeBox.getItems().add("Me");
        assigneeBox.setValue("Me");
        assigneeGroup.getChildren().addAll(assigneeLabel, assigneeBox);

        row2.getChildren().addAll(tagGroup, assigneeGroup);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button createBtn = new Button("Create Task");
        createBtn.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        createBtn.setOnAction(e -> {
            String title = titleField.getText();
            if (title != null && !title.trim().isEmpty()) {
                try {
                    LocalDate dueDate = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        
                    com.taskmanager.model.User assignee = null;
                    if ("Me".equals(assigneeBox.getValue())) {
                        assignee = com.taskmanager.util.UserSession.getInstance().getCurrentUser();
                    }

                    taskService.createTask(projectId, title, descArea.getText(), dueDate, assignee);

                    if (onSuccess != null)
                        onSuccess.run();
                    dialog.close();
                } catch (Exception ex) {
                    System.err.println("Error creating task: " + ex.getMessage());
                }
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, createBtn);

        dialogVbox.getChildren().addAll(titleLabel, titleGroup, descGroup, row1, row2, buttonBox);
        root.getChildren().add(dialogVbox);

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(dialogScene);

        if (Stage.getWindows().stream().filter(javafx.stage.Window::isShowing).findFirst().isPresent()) {
            javafx.stage.Window owner = Stage.getWindows().stream().filter(javafx.stage.Window::isShowing).findFirst()
                    .get();
            dialog.setX(owner.getX());
            dialog.setY(owner.getY());
            dialog.setWidth(owner.getWidth());
            dialog.setHeight(owner.getHeight());
        } else {
            dialog.setWidth(800);
            dialog.setHeight(600);
        }

        dialog.show();
    }
}
