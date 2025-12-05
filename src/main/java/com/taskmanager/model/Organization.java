package com.taskmanager.model;

import com.taskmanager.model.interfaces.BaseEntity;
import com.taskmanager.model.interfaces.WorkComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Organization implements BaseEntity, WorkComponent {

    private int id;
    private String orgName;
    private String code;

    private List<Project> projects = new ArrayList<>();
    private List<User> members = new ArrayList<>();

    public Organization() {
    }

    public Organization(int id, String name, String code) {
        this.id = id;
        this.orgName = name;
        this.code = code;
    }

    public Organization(int id, String name) {
        this(id, name, null);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    // --- Getters & Setters ---
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public void setProjects(List<Project> projects) {
        this.projects = projects;
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

    public void setMembers(List<User> members) {
        this.members = members;
    }

    // --- Implementasi WorkComponent ---
    @Override
    public String getName() {
        return getOrgName();
    }

    @Override
    public double getCompletionPercentage() {
        if (projects.isEmpty()) {
            return 0.0;
        }

        double totalPercentage = 0.0;
        for (Project project : projects) {
            totalPercentage += project.getCompletionPercentage();
        }

        return totalPercentage / projects.size();
    }

    @Override
    public String toString() {
        return "Organization{id=" + id + ", name='" + orgName + "', code='" + code + "'}";
    }
}