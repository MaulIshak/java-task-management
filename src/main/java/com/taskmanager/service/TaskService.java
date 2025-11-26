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
        this.taskDAO = new TaskDAO();
        this.userDAO = new UserDAO();
    }
    public TaskService(TaskDAO taskDAO, UserDAO userDAO) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
    }

    public Task createTask(int projectId, String title, String description, LocalDate dueDate, User assignee) throws Exception {
        // Validasi
        if (title == null || title.isEmpty()) throw new Exception("Title required");

        Task newTask = new TaskBuilder(projectId, title)
                .setDescription(description)
                .setDueDate(dueDate)
                .setAssignee(assignee)
                .setStatus(TaskStatus.TODO) // Default status
                .build();

        return taskDAO.save(newTask);
    }

    public void updateTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
        taskDAO.save(task);
    }

    public List<Task> getTasksByProject(int projectId) {
        List<Task> tasks = taskDAO.findByProjectId(projectId);

        // Isi data assignee
        for (Task t : tasks) {
            if (t.getAssignee() != null) {
                userDAO.findById(t.getAssignee().getId())
                        .ifPresent(t::setAssignee);
            }
        }
        return tasks;
    }
}