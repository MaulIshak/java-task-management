package com.taskmanager.model;
import com.taskmanager.model.interfaces.WorkComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Organization implements WorkComponent {

    private final int id;
    private final String orgName;

    private final List<Project> projects = new ArrayList<>();
    private final List<User> members = new ArrayList<>();

    public Organization(int id, String name) {
        this.id = id;
        this.orgName = name;
    }

    public int getId() {
        return id;
    }

    public String getOrgName() {
        return orgName;
    }

    // --- Project operations ---
    public void addProject(Project project) {
        projects.add(project);
    }

    public void removeProject(Project project) {
        projects.remove(project);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    // --- Member operations ---
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
        return orgName;
    }

    @Override
    public void showDetails() {
        System.out.println("Organization: " + orgName);
        for (Project project : projects) {
            project.showDetails();
        }
    }
}
