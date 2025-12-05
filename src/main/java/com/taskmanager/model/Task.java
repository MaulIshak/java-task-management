package com.taskmanager.model;

import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.model.interfaces.BaseEntity;
import com.taskmanager.model.interfaces.WorkComponent;

import java.time.LocalDate;

public class Task implements WorkComponent, BaseEntity {
    private int id;
    private int projectId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private User assignee;

    public Task() {
    }

    Task(TaskBuilder builder) {
        this.id = builder.id;
        this.projectId = builder.projectId;
        this.title = builder.title;
        this.description = builder.description;
        this.dueDate = builder.dueDate;
        this.status = builder.status;
        this.assignee = builder.assignee;
    }

    // --- BaseEntity Implementation ---
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    // --- Getters & Setters ---
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    @Override
    public String getName() {
        return getTitle();
    }

    @Override
    public double getCompletionPercentage() {
        if (status == null)
            return 0.0;

        return switch (status) {
            case DONE -> 100.0;
            case ON_PROGRESS -> 0.0;
            default -> 0.0;
        };
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}