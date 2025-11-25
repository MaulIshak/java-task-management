-- ==========================================================
-- SKEMA DATABASE TASK MANAGEMENT
-- Mapping: Java Models -> MySQL Tables
-- ==========================================================
DROP DATABASE IF EXISTS task_management_db;

CREATE DATABASE task_management_db;

USE task_management_db;

-- 1. TABLE USERS
CREATE TABLE users
(
    id            INT auto_increment PRIMARY KEY,-- Ubah ke INT Auto Increment
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABLE ORGANIZATIONS
CREATE TABLE organizations
(
    id         INT auto_increment PRIMARY KEY,-- Ubah ke INT Auto Increment
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLE ORGANIZATION_MEMBERS
CREATE TABLE organization_members
(
    organization_id INT NOT NULL,
    user_id         INT NOT NULL,
    role            VARCHAR(20) DEFAULT 'MEMBER',
    joined_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (organization_id, user_id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE
        CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. TABLE PROJECTS
CREATE TABLE projects
(
    id              INT auto_increment PRIMARY KEY,
    organization_id INT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE
        CASCADE
);

-- 5. TABLE TASKS
CREATE TABLE tasks
(
    id          INT auto_increment PRIMARY KEY,
    project_id  INT NOT NULL,
    assignee_id INT,
    title       VARCHAR(150) NOT NULL,
    description TEXT,
    due_date    DATE,
    status      ENUM('todo', 'on progress', 'done') DEFAULT 'todo',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ==========================================================
-- DUMMY SEED DATA (Dengan ID Integer Eksplisit)
-- ==========================================================
-- Insert Users (ID akan otomatis 1 dan 2)
INSERT INTO users
(id,
 name,
 email,
 password_hash)
VALUES      (1,
             'Alex Johnson',
             'alex@zenith.com',
             'secret'),
            (2,
             'Sarah Miller',
             'sarah@zenith.com',
             'secret');

-- Insert Organization (ID = 1)
INSERT INTO organizations
(id,
 name)
VALUES      (1,
             'Innovate Inc.');

-- Insert Members
INSERT INTO organization_members
(organization_id,
 user_id,
 role)
VALUES      (1,
             1,
             'OWNER'),
            (1,
             2,
             'MEMBER');

-- Insert Project (ID = 1)
INSERT INTO projects
(id,
 organization_id,
 name,
 description)
VALUES      (1,
             1,
             'Phoenix Project',
             'A complete overhaul of the main application.');

-- Insert Tasks (ID = 1, 2)
INSERT INTO tasks
(id,
 project_id,
 assignee_id,
 title,
 description,
 due_date,
 status)
VALUES      (1,
             1,
             1,
             'Implement Login',
             'Create login form using JavaFX',
             '2025-11-30',
             'todo'),
            (2,
             1,
             2,
             'Fix Database Bug',
             'Connection timeout issue',
             '2025-11-28',
             'on progress');