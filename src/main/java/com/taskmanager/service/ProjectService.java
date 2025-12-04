package com.taskmanager.service;

import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.dao.TaskDAO;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;

import java.util.List;
import java.util.NoSuchElementException;

public class ProjectService {

    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;

    public ProjectService() {
        this(new ProjectDAO(), new TaskDAO());
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
        return projectDAO.save(project);
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
    }

    public void deleteProject(int projectId) {
        projectDAO.delete(projectId);
    }
}