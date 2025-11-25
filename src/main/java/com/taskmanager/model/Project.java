package com.taskmanager.model;

import com.taskmanager.model.interfaces.WorkComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Project implements WorkComponent {

    private final int id;
    private final String projectName;
    private final String description;

    private final List<Task> tasks = new ArrayList<>();
    private final List<User> members = new ArrayList<>();

    public Project(int id, String name, String desc) {
        this.id = id;
        this.projectName = name;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    // --- Task Operations ---
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Return list read-only untuk keamanan
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    // --- Member Operations ---
    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    public List<User> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public String getName() {
        return projectName;
    }

    @Override
    public void showDetails() {
        System.out.println("Project: " + projectName);
        for (Task t : tasks) {
            t.showDetails();
        }
    }
}
