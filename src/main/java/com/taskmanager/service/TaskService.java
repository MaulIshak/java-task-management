package com.taskmanager.service;

import com.taskmanager.dao.TaskDAO;
import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskBuilder;
import com.taskmanager.model.User;
import com.taskmanager.model.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public class TaskService implements com.taskmanager.model.interfaces.Subject {

    private static TaskService instance;

    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final java.util.List<com.taskmanager.model.interfaces.Observer> observers = new java.util.ArrayList<>();

    public TaskService() {
        this(new TaskDAO(), new UserDAO());
    private TaskService() {
        this.taskDAO = new TaskDAO();
        this.userDAO = new UserDAO();
    }


    public static synchronized TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
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

        Task savedTask = taskDAO.save(newTask);
        notifyObservers();
        return savedTask;
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
        notifyObservers();
    }

    public void deleteTask(int taskId) {
        taskDAO.delete(taskId);
        notifyObservers();
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

    @Override
    public void registerObserver(com.taskmanager.model.interfaces.Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(com.taskmanager.model.interfaces.Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (com.taskmanager.model.interfaces.Observer observer : observers) {
            observer.update();
        }
    }
}