package com.taskmanager.model;

public class User {
    private final int id;
    private final String name;
    private final String email;
    private final String avatarPath; // optional

    public User(int id, String name, String email) {
        this(id, name, email, null);
    }

    public User(int id, String name, String email, String avatarPath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarPath = avatarPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    @Override
    public String toString() {
        return name;
    }
}
