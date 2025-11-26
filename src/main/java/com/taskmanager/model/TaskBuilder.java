package com.taskmanager.model;

import com.taskmanager.model.enums.TaskStatus;
import java.time.LocalDate;

public class TaskBuilder {

    // Fields
    int id;
    int projectId;
    String title;
    String description = "";
    LocalDate dueDate = LocalDate.now().plusDays(7); // Default 1 minggu
    TaskStatus status = TaskStatus.TODO;
    User assignee;

    public TaskBuilder(int projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }

    public TaskBuilder(int id, int projectId, String title) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
    }

    public TaskBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder setStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskBuilder setAssignee(User assignee) {
        this.assignee = assignee;
        return this;
    }

    public Task build() {
        return new Task(this);
    }
}