package com.taskmanager.view;

import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.service.TaskService;
import com.taskmanager.util.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.stream.Collectors;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ProjectDetailView extends VBox implements com.taskmanager.model.interfaces.Observer, View {

    private static final Logger LOGGER = Logger.getLogger(ProjectDetailView.class.getName());

    private final TaskService taskService;
    private Project project;

    public ProjectDetailView(TaskService taskService) {
        this.taskService = taskService;
        setSpacing(20);
        setPadding(new Insets(30));
        getStyleClass().add("dashboard-view");
        UserSession.getInstance().registerObserver(this);
    }

    public void setProject(Project project) {
        this.project = project;
        render();
    }

    @Override
    public void update() {
        javafx.application.Platform.runLater(() -> {
            if (UserSession.getInstance().isLoggedIn()) {
                if (project != null) {
                    render();
                }
            } else {
                getChildren().clear();
                this.project = null;
            }
        });
    }

    @Override
    public void render() {
        getChildren().clear();

        if (!UserSession.getInstance().isLoggedIn()) {
            return;
        }

        if (project == null)
            return;

        // --- Header Section ---
        VBox headerContainer = new VBox(10);
        headerContainer.getStyleClass().add("project-header");

        HBox topHeader = new HBox();
        topHeader.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(project.getName());
        titleLabel.getStyleClass().add("project-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addTaskButton = new Button("+ Add Task");
        addTaskButton.getStyleClass().add("primary-button"); // Changed to generic primary button class
        addTaskButton.setOnAction(e -> showCreateTaskModal());

        topHeader.getChildren().addAll(titleLabel, spacer, addTaskButton);

        Label descriptionLabel = new Label(
                project.getDescription() != null ? project.getDescription() : "No description");
        descriptionLabel.getStyleClass().add("project-description");

        headerContainer.getChildren().addAll(topHeader, descriptionLabel);
        getChildren().add(headerContainer);

        // --- Kanban Board ---
        HBox kanbanBoard = new HBox(30);
        kanbanBoard.getStyleClass().add("kanban-board");
        VBox.setVgrow(kanbanBoard, Priority.ALWAYS);
        kanbanBoard.setMinHeight(Region.USE_PREF_SIZE);
        kanbanBoard.prefHeightProperty().bind(this.heightProperty());

        List<Task> allTasks = taskService.getTasksByProject(project.getId());

        List<Task> todoTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.TODO)
                .toList();
        List<Task> inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.ON_PROGRESS)
                .toList();
        List<Task> doneTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .toList();

        VBox todoCol = createColumn("Todo", todoTasks);
        VBox inProgressCol = createColumn("In Progress", inProgressTasks);
        VBox doneCol = createColumn("Done", doneTasks);

        todoCol.prefWidthProperty().bind(kanbanBoard.widthProperty().divide(3).subtract(20));
        inProgressCol.prefWidthProperty().bind(kanbanBoard.widthProperty().divide(3).subtract(20));
        doneCol.prefWidthProperty().bind(kanbanBoard.widthProperty().divide(3).subtract(20));

        kanbanBoard.getChildren().addAll(todoCol, inProgressCol, doneCol);

        getChildren().add(kanbanBoard);
    }

    private VBox createColumn(String title, List<Task> tasks) {
        VBox column = new VBox(10);
        column.setMinWidth(250);
        column.getStyleClass().add("kanban-column");
        VBox.setVgrow(column, Priority.ALWAYS);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("column-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label(String.valueOf(tasks.size()));
        countLabel.getStyleClass().add("column-count-badge");

        header.getChildren().addAll(titleLabel, spacer, countLabel);
        column.getChildren().add(header);

        // Task List wrapped in ScrollPane
        VBox taskList = new VBox(10);

        for (Task task : tasks) {
            taskList.getChildren().add(createTaskCard(task));
        }

        ScrollPane scrollPane = new ScrollPane(taskList);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int taskId = Integer.parseInt(db.getString());
                    TaskStatus newStatus = null;
                    if ("Todo".equals(title))
                        newStatus = TaskStatus.TODO;
                    else if ("In Progress".equals(title))
                        newStatus = TaskStatus.ON_PROGRESS;
                    else if ("Done".equals(title))
                        newStatus = TaskStatus.DONE;

                    if (newStatus != null) {
                        updateTaskStatus(taskId, newStatus);
                        success = true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        column.getChildren().add(scrollPane);
        return column;
    }

    private VBox createTaskCard(Task task) {
        VBox card = new VBox(10);
        card.getStyleClass().add("kanban-task-card");

        // Header: Title and Menu
        HBox header = new HBox();
        header.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");
        titleLabel.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        MenuButton optionsMenu = new MenuButton("...");
        optionsMenu.getStyleClass().add("task-menu-button");

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> LOGGER.info("Edit task feature not implemented yet. Task ID: " + task.getId()));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            try {
                taskService.deleteTask(task.getId());
                render();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        optionsMenu.getItems().addAll(editItem, deleteItem);
        header.getChildren().addAll(titleLabel, spacer, optionsMenu);
        card.getChildren().add(header);

        // Description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            Label descLabel = new Label(task.getDescription());
            descLabel.getStyleClass().add("task-description");
            card.getChildren().add(descLabel);
        }

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label tagLabel = new Label("Feature");
        tagLabel.getStyleClass().addAll("task-tag", "tag-feature");

        Label dateLabel = new Label(task.getDueDate() != null ? task.getDueDate().toString() : "No Date");
        dateLabel.getStyleClass().add("task-date");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        // Avatar placeholder
        Circle avatar = new Circle(12);
        avatar.getStyleClass().add("task-avatar");

        footer.getChildren().addAll(tagLabel, dateLabel, footerSpacer, avatar);
        card.getChildren().add(footer);

        // Drag and Drop for Card (Source)
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
            Task task = project.getTasks().stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
            if (task == null) {
                task = taskService.getTasksByProject(project.getId()).stream().filter(t -> t.getId() == taskId)
                        .findFirst().orElse(null);
            }

            if (task != null) {
                taskService.updateTaskStatus(task, newStatus);
                render();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCreateTaskModal() {
        if (project != null) {
            new CreateTaskModal(taskService, project.getId(), this::render).show();
        } else {
            LOGGER.log(Level.WARNING, "Cannot create task: No project selected.");
        }
    }
}
