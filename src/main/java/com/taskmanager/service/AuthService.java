package com.taskmanager.service;

import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.User;
import com.taskmanager.util.PasswordUtils;
import com.taskmanager.util.UserSession;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String name, String email, String plainPassword) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already registered!");
        }

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        User newUser = new User(0, name, email, hashedPassword);
        return userDAO.save(newUser);
    }

    public User login(String email, String plainPassword) {
        if (email == null || plainPassword == null) {
            throw new IllegalArgumentException("Email and password cannot be null");
        }

        // Cari user
        Optional<User> userOpt = userDAO.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new SecurityException("Invalid credentials");
        }

        User user = userOpt.get();

        // Verifikasi Password
        if (PasswordUtils.verifyPassword(plainPassword, user.getPasswordHash())) {
            UserSession.getInstance().startSession(user);
            return user;
        } else {
            throw new SecurityException("Invalid credentials");
        }
    }
}