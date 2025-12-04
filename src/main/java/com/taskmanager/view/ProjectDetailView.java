package com.taskmanager.view;

import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.service.ProjectService;
import com.taskmanager.service.TaskService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.List;

public class ProjectDetailView extends VBox {

    private final int projectId;
    private final ProjectService projectService;
    private final TaskService taskService;
    private Project project;

    private VBox todoColumn;
    private VBox inProgressColumn;
    private VBox doneColumn;

    public ProjectDetailView(int projectId, ProjectService projectService, TaskService taskService) {
        this.projectId = projectId;
        this.projectService = projectService;
        this.taskService = taskService;

        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("dashboard-view");

        loadProjectData();
    }

    private void loadProjectData() {
        getChildren().clear();
        try {
            project = projectService.getProjectWithTasks(projectId);

            // Header
            HBox header = new HBox(20);
            header.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(project.getName());
            nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Label descLabel = new Label(project.getDescription());
            descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button addTaskBtn = new Button("New Task");
            addTaskBtn.setText("+ New Task"); // Simple text instead of icon
            addTaskBtn.getStyleClass().add("primary-button"); // Ensure this style exists or use inline
            addTaskBtn.setStyle(
                    "-fx-background-color: #2962ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
            addTaskBtn.setOnAction(e -> showCreateTaskModal());

            header.getChildren().addAll(nameLabel, spacer, addTaskBtn);
            getChildren().addAll(header, descLabel);

            // Kanban Board
            HBox board = new HBox(20);
            HBox.setHgrow(board, Priority.ALWAYS);
            VBox.setVgrow(board, Priority.ALWAYS);

            todoColumn = createColumn("Todo", TaskStatus.TODO);
            inProgressColumn = createColumn("In Progress", TaskStatus.ON_PROGRESS);
            doneColumn = createColumn("Done", TaskStatus.DONE);

            // Distribute tasks
            if (project.getTasks() != null) {
                for (Task task : project.getTasks()) {
                    VBox card = createTaskCard(task);
                    switch (task.getStatus()) {
                        case TODO:
                            todoColumn.getChildren().add(card);
                            break;
                        case ON_PROGRESS:
                            inProgressColumn.getChildren().add(card);
                            break;
                        case DONE:
                            doneColumn.getChildren().add(card);
                            break;
                    }
                }
            }

            board.getChildren().addAll(wrapColumn(todoColumn), wrapColumn(inProgressColumn), wrapColumn(doneColumn));
            getChildren().add(board);

        } catch (Exception e) {
            e.printStackTrace();
            getChildren().add(new Label("Error loading project: " + e.getMessage()));
        }
    }

    private VBox wrapColumn(VBox column) {
        VBox wrapper = new VBox(column);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        wrapper.setStyle("-fx-background-color: #f4f5f7; -fx-background-radius: 5;");
        wrapper.setPadding(new Insets(10));
        return wrapper;
    }

    private VBox createColumn(String title, TaskStatus status) {
        VBox column = new VBox(10);
        column.setMinWidth(250);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #5e6c84;");
        column.getChildren().add(titleLabel);

        // Drag over logic
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Drop logic
        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int taskId = Integer.parseInt(db.getString());
                updateTaskStatus(taskId, status);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return column;
    }

    private VBox createTaskCard(Task task) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1); -fx-cursor: move;");

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        title.setWrapText(true);

        Label desc = new Label(task.getDescription());
        desc.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        desc.setWrapText(true);

        HBox meta = new HBox(10);
        Label date = new Label(task.getDueDate() != null ? task.getDueDate().toString() : "No date");
        date.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        Label assignee = new Label(task.getAssignee() != null ? task.getAssignee().getName() : "Unassigned");
        assignee.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        meta.getChildren().addAll(date, assignee);
        card.getChildren().addAll(title, desc, meta);

        // Drag detection
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            event.consume();
        });

        return card;
    }

    private void updateTaskStatus(int taskId, TaskStatus newStatus) {
        try {
            // Find task object (optimization: keep map or iterate)
            // For simplicity, fetch or iterate current list
            Task task = project.getTasks().stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
            if (task != null) {
                taskService.updateTaskStatus(task, newStatus);
                loadProjectData(); // Refresh UI
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCreateTaskModal() {
        new CreateTaskModal(taskService, projectId, this::loadProjectData).show();
    }
}
