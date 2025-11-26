package com.taskmanager.util;

import com.taskmanager.model.User;

import com.taskmanager.model.interfaces.Observer;
import com.taskmanager.model.interfaces.Subject;

import java.util.ArrayList;
import java.util.List;

public class UserSession implements Subject {
    private static UserSession instance;
    private User currentUser;
    private List<Observer> observers = new ArrayList<>();

    private UserSession() {
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // --- Session Management Methods ---
    public void startSession(User user) {
        this.currentUser = user;
        notifyObservers();
    }

    public void endSession() {
        this.currentUser = null;
        notifyObservers();
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in! Check isLoggedIn() before calling this.");
        }
        return currentUser;
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}