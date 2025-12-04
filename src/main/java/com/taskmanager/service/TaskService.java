package com.taskmanager.service;

import com.taskmanager.dao.TaskDAO;
import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskBuilder;
import com.taskmanager.model.User;
import com.taskmanager.model.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public class TaskService {

    private final TaskDAO taskDAO;
    private final UserDAO userDAO;

    public TaskService() {
        this(new TaskDAO(), new UserDAO());
    }

    public TaskService(TaskDAO taskDAO, UserDAO userDAO) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
    }

    public Task createTask(int projectId, String title, String description, LocalDate dueDate, User assignee) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        Task newTask = new TaskBuilder(projectId, title)
                .setDescription(description)
                .setDueDate(dueDate)
                .setAssignee(assignee)
                .setStatus(TaskStatus.TODO) // Default status
                .build();

        return taskDAO.save(newTask);
    }


    public void updateTaskStatus(Task task, TaskStatus newStatus) {
        if (task == null) {
            throw new IllegalArgumentException("Task to update cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        task.setStatus(newStatus);
        taskDAO.save(task);
    }

    public List<Task> getTasksByProject(int projectId) {
        List<Task> tasks = taskDAO.findByProjectId(projectId);

        // Populate Assignee Details
        for (Task t : tasks) {
            if (t.getAssignee() != null) {
                userDAO.findById(t.getAssignee().getId())
                        .ifPresent(t::setAssignee);
            }
        }
        return tasks;
    }
}