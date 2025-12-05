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
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateTaskModal {

    private static final Logger LOGGER = Logger.getLogger(CreateTaskModal.class.getName());

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

        TextField titleField = createTitleInput();
        VBox titleGroup = new VBox(5);
        titleGroup.getChildren().addAll(new Label("Title"), titleField);

        TextArea descArea = createDescriptionInput();
        VBox descGroup = new VBox(5);
        descGroup.getChildren().addAll(new Label("Description"), descArea);

        DatePicker datePicker = new DatePicker();
        ComboBox<String> statusBox = createStatusBox();
        HBox row1 = createRow1(datePicker, statusBox);

        ComboBox<String> tagBox = createTagBox();
        ComboBox<String> assigneeBox = createAssigneeBox();
        HBox row2 = createRow2(tagBox, assigneeBox);

        HBox buttonBox = createButtonBox(dialog, titleField, descArea, datePicker, assigneeBox);

        dialogVbox.getChildren().addAll(titleLabel, titleGroup, descGroup, row1, row2, buttonBox);
        root.getChildren().add(dialogVbox);

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(dialogScene);

        positionDialog(dialog);

        dialog.show();
    }

    private TextField createTitleInput() {
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        titleField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        return titleField;
    }

    private TextArea createDescriptionInput() {
        TextArea descArea = new TextArea();
        descArea.setPromptText("Task description...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-background-radius: 5; -fx-border-color: #ccc; -fx-border-radius: 5;");
        return descArea;
    }

    private ComboBox<String> createStatusBox() {
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("TODO", "ON_PROGRESS", "DONE");
        statusBox.setValue("TODO");
        return statusBox;
    }

    private HBox createRow1(DatePicker datePicker, ComboBox<String> statusBox) {
        HBox row1 = new HBox(20);
        VBox dateGroup = new VBox(5);
        dateGroup.getChildren().addAll(new Label("Due Date"), datePicker);

        VBox statusGroup = new VBox(5);
        statusGroup.getChildren().addAll(new Label("Status"), statusBox);

        row1.getChildren().addAll(dateGroup, statusGroup);
        return row1;
    }

    private ComboBox<String> createTagBox() {
        ComboBox<String> tagBox = new ComboBox<>();
        tagBox.getItems().addAll("Feature", "Bug", "Design", "Refactor");
        tagBox.setValue("Feature");
        return tagBox;
    }

    private ComboBox<String> createAssigneeBox() {
        ComboBox<String> assigneeBox = new ComboBox<>();
        assigneeBox.getItems().add("Me");
        assigneeBox.setValue("Me");
        return assigneeBox;
    }

    private HBox createRow2(ComboBox<String> tagBox, ComboBox<String> assigneeBox) {
        HBox row2 = new HBox(20);
        VBox tagGroup = new VBox(5);
        tagGroup.getChildren().addAll(new Label("Tag"), tagBox);

        VBox assigneeGroup = new VBox(5);
        assigneeGroup.getChildren().addAll(new Label("Assignee"), assigneeBox);

        row2.getChildren().addAll(tagGroup, assigneeGroup);
        return row2;
    }

    private HBox createButtonBox(Stage dialog, TextField titleField, TextArea descArea, DatePicker datePicker,
            ComboBox<String> assigneeBox) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button createBtn = new Button("Create Task");
        createBtn.setStyle(
                "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        createBtn.setOnAction(e -> handleCreateTask(dialog, titleField, descArea, datePicker, assigneeBox));

        buttonBox.getChildren().addAll(cancelBtn, createBtn);
        return buttonBox;
    }

    private void handleCreateTask(Stage dialog, TextField titleField, TextArea descArea, DatePicker datePicker,
            ComboBox<String> assigneeBox) {
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
                LOGGER.log(Level.SEVERE, "Error creating task", ex);
            }
        }
    }

    private void positionDialog(Stage dialog) {
        Optional<Window> owner = Window.getWindows().stream().filter(Window::isShowing).findFirst();
        if (owner.isPresent()) {
            Window window = owner.get();
            dialog.setX(window.getX());
            dialog.setY(window.getY());
            dialog.setWidth(window.getWidth());
            dialog.setHeight(window.getHeight());
        } else {
            dialog.setWidth(800);
            dialog.setHeight(600);
        }
    }
}
