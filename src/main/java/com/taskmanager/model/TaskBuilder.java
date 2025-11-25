package com.taskmanager.model;
import com.taskmanager.model.enums.TaskStatus;

import java.time.LocalDate;

public class TaskBuilder {

    // Required
    final int id;
    final String title;
    final User owner;

    // Optional
    String description = "";
    LocalDate dueDate = LocalDate.now().plusDays(1);
    TaskStatus status = TaskStatus.TODO;
    User assignee;

    public TaskBuilder(int id, String title, User owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.assignee = owner; // default
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
