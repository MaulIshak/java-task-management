package com.taskmanager.service;

import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.dao.TaskDAO;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;

public class ProjectService {

    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO; // untuk fetch tasks

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
    }

    public ProjectService(ProjectDAO projectDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
    }

    // 1. Create Project
    public Project createProject(int organizationId, String name, String description) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Project name cannot be empty");
        }
        Project project = new Project(0, organizationId, name, description);
        return projectDAO.save(project);
    }

    // 2. Get List Project by Org (Ringan - Tanpa Task)
    public List<Project> getProjectsByOrganization(int organizationId) {
        return projectDAO.findByOrganizationId(organizationId);
    }

    // 3. Get Full Project Details (Berat - Dengan Task)
    public Project getProjectWithTasks(int projectId) throws Exception {
        Optional<Project> projectOpt = projectDAO.findById(projectId);

        if (projectOpt.isEmpty()) {
            throw new Exception("Project not found");
        }

        Project project = projectOpt.get();

        // Fetch tasks from DB
        List<Task> tasks = taskDAO.findByProjectId(projectId);
        project.setTasks(tasks); // Pasang ke object project

        return project;
    }

    // 4. Update Project
    public void updateProject(Project project, String newName, String newDesc) throws Exception {
        if (newName == null || newName.trim().isEmpty()) {
            throw new Exception("New name cannot be empty");
        }

        project.setName(newName);
        project.setDescription(newDesc);

        projectDAO.save(project); // Auto Update karena ID != 0
    }

    // 5. Delete Project
    public void deleteProject(int projectId) {
        projectDAO.delete(projectId);
    }
}