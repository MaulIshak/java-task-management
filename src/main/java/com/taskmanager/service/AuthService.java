package com.taskmanager.service;

import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.User;
import com.taskmanager.util.PasswordUtils;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // Return User object jika sukses, throw Exception jika gagal
    public User register(String name, String email, String plainPassword) throws Exception {
        // Validasi Input
        if (name == null || name.isEmpty()) throw new Exception("Name cannot be empty");
        if (email == null || email.isEmpty()) throw new Exception("Email cannot be empty");
        if (plainPassword == null || plainPassword.length() < 6) throw new Exception("Password must be at least 6 characters");

        // 2. Cek apakah email sudah ada
        if (userDAO.findByEmail(email).isPresent()) {
            throw new Exception("Email already registered!");
        }

        // Hash Password
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        // Simpan ke DB
        User newUser = new User(0, name, email, hashedPassword);
        return userDAO.save(newUser);
    }

    public User login(String email, String plainPassword) throws Exception {
        // 1. Cari user berdasarkan email
        Optional<User> userOpt = userDAO.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new Exception("User not found / Invalid credentials");
        }

        User user = userOpt.get();

        // 2. Verifikasi Password
        if (PasswordUtils.verifyPassword(plainPassword, user.getPasswordHash())) {
            return user;
        } else {
            throw new Exception("Invalid password");
        }
    }
}