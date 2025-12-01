package com.taskmanager.model;

import com.taskmanager.model.interfaces.BaseEntity;
import com.taskmanager.model.interfaces.WorkComponent;

import java.util.ArrayList;
import java.util.List;

public class Project implements WorkComponent, BaseEntity {

    private int id;
    private int organizationId; // Foreign Key ke Organization
    private String name;
    private String description;

    // Relasi
    private List<Task> tasks = new ArrayList<>();

    public Project() {}

    public Project(int id, int organizationId, String name, String description) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
    }

    // --- BaseEntity Impl ---
    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }

    // --- Getters & Setters ---
    public int getOrganizationId() { return organizationId; }
    public void setOrganizationId(int organizationId) { this.organizationId = organizationId; }

    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // --- List Operations ---
    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    @Override
    public String getName() { return name; }

    @Override
    public void showDetails() {
        System.out.println("Project: " + name);
    }

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "'}";
    }
}