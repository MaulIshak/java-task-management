package com.taskmanager.view;

import java.util.ArrayList;
import java.util.List;

import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.model.interfaces.Subject;

public class AppState implements Subject {
    private static final AppState instance = new AppState();
    private final List<Observer> observers = new ArrayList<>();

    private Organization currentOrganization;
    private Project currentProject;

    private ViewName currentView;

    public static AppState getInstance() {
        return instance;
    }

    public void setCurrentOrganization(Organization org) {
        this.currentOrganization = org;
        notifyObservers();
    }

    public Organization getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentProject(Project project) {
        this.currentProject = project;
        notifyObservers();
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void switchView(ViewName view) {
        this.currentView = view;
        notifyObservers();
    }

    public ViewName getCurrentView() {
        return currentView;
    }

    public void logout() {
        this.currentOrganization = null;
        this.currentProject = null;
        this.currentView = null;
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
}
