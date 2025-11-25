package com.taskmanager.model;

import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.model.interfaces.WorkComponent;

import java.time.LocalDate;

// --- COMPOSITE PATTERN: LEAF (TASK) ---
public class Task implements WorkComponent {

    private final int id;
    private final String title;
    private final String description;
    private final LocalDate dueDate;
    private final TaskStatus status;
    private final User assignee;
    private final User owner;

    Task(TaskBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.dueDate = builder.dueDate;
        this.status = builder.status;
        this.assignee = builder.assignee;
        this.owner = builder.owner;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public void showDetails() {
        System.out.println("Task: " + title + " [" + status + "]");
    }

    // Getters biasa
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public TaskStatus getStatus() { return status; }
    public User getAssignee() { return assignee; }
    public User getOwner() { return owner; }
}
