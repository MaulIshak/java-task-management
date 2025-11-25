package com.taskmanager.util;

import com.taskmanager.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // --- Session Management Methods ---
    public void startSession(User user) {
        this.currentUser = user;
    }

    public void endSession() {
        this.currentUser = null;
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
}