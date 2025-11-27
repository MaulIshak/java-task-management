package com.taskmanager.model;

import com.taskmanager.model.interfaces.BaseEntity;
import com.taskmanager.model.interfaces.WorkComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Mengimplementasikan kedua interface: WorkComponent (untuk getName, showDetails) dan BaseEntity (untuk DAO)
public class Organization implements BaseEntity, WorkComponent {

    // 1. Hapus 'final' agar ID bisa diubah oleh setId()
    private int id;
    private final String orgName;

    private final List<Project> projects = new ArrayList<>();
    private final List<User> members = new ArrayList<>();

    public Organization(int id, String name) {
        this.id = id;
        this.orgName = name;
    }

    // --- Implementasi BaseEntity ---
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        // 2. Tambahkan implementasi setter ID
        this.id = id;
    }
    // --------------------------------

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

    // --- Implementasi WorkComponent ---
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