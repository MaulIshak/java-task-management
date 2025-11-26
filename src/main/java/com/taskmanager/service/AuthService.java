package com.taskmanager.service;

import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.User;
import com.taskmanager.util.PasswordUtils;
import com.taskmanager.util.UserSession;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Return User object jika sukses, throw Exception jika gagal
    public User register(String name, String email, String plainPassword) throws Exception {
        // Validasi Input
        if (name == null || name.isEmpty()) throw new Exception("Name cannot be empty");
        if (email == null || email.isEmpty()) throw new Exception("Email cannot be empty");
        if (plainPassword == null || plainPassword.length() < 6) throw new Exception("Password must be at least 6 characters");

        if (userDAO.findByEmail(email).isPresent()) {
            throw new Exception("Email already registered!");
        }

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        // Simpan ke DB
        User newUser = new User(0, name, email, hashedPassword);
        return userDAO.save(newUser);
    }

    public User login(String email, String plainPassword) throws Exception {
        // Cari user berdasarkan email
        Optional<User> userOpt = userDAO.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new Exception("User not found / Invalid credentials");
        }

        User user = userOpt.get();

        // Verifikasi Password
        if (PasswordUtils.verifyPassword(plainPassword, user.getPasswordHash())) {
            UserSession.getInstance().startSession(user);
            return user;
        } else {
            throw new Exception("Invalid password");
        }
    }
}