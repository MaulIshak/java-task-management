package com.taskmanager.service;

import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.dao.TaskDAO;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;

import java.util.List;
import java.util.NoSuchElementException;

public class ProjectService implements com.taskmanager.model.interfaces.Subject {

    private static ProjectService instance;

    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final java.util.List<com.taskmanager.model.interfaces.Observer> observers = new java.util.ArrayList<>();

    public ProjectService() {
        this(new ProjectDAO(), new TaskDAO());
    }

    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }

    public ProjectService(ProjectDAO projectDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
    }

    public Project createProject(int organizationId, String name, String description) {
        // Validasi input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        Project project = new Project(0, organizationId, name, description);
        Project savedProject = projectDAO.save(project);
        notifyObservers();
        return savedProject;
    }

    public List<Project> getProjectsByOrganization(int organizationId) {
        return projectDAO.findByOrganizationId(organizationId);
    }

    public Project getProjectWithTasks(int projectId) {
        // Menggunakan idiom Optional.orElseThrow agar lebih bersih
        Project project = projectDAO.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found with ID: " + projectId));

        // Fetch tasks from DB
        List<Task> tasks = taskDAO.findByProjectId(projectId);
        project.setTasks(tasks);

        return project;
    }

    public void updateProject(Project project, String newName, String newDesc) {
        // Validasi argumen object
        if (project == null) {
            throw new IllegalArgumentException("Project object cannot be null");
        }

        // Validasi input data baru
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be empty");
        }

        project.setName(newName);
        project.setDescription(newDesc);

        projectDAO.save(project);
        notifyObservers();
    }

    public void deleteProject(int projectId) {
        projectDAO.delete(projectId);
        notifyObservers();
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