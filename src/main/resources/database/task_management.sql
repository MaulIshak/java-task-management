-- =========================================
-- 1. TABLE USERS
-- =========================================
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- 2. TABLE ORGANIZATIONS
-- =========================================
CREATE TABLE organizations (
                               id SERIAL PRIMARY KEY,
                               name VARCHAR(100) NOT NULL,
                               code VARCHAR(6) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- 3. TABLE ORGANIZATION_MEMBERS
-- =========================================
CREATE TABLE organization_members (
                                      organization_id INT NOT NULL,
                                      user_id INT NOT NULL,
                                      role VARCHAR(20) DEFAULT 'MEMBER',
                                      joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (organization_id, user_id),
                                      FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================
-- 4. TABLE PROJECTS
-- =========================================
CREATE TABLE projects (
                          id SERIAL PRIMARY KEY,
                          organization_id INT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- =========================================
-- 5. TABLE TASKS
-- =========================================
CREATE TABLE tasks (
                       id SERIAL PRIMARY KEY,
                       project_id INT NOT NULL,
                       assignee_id INT,
                       title VARCHAR(150) NOT NULL,
                       description TEXT,
                       due_date DATE,
                       status VARCHAR(20) NOT NULL DEFAULT 'todo'
                           CHECK (status IN ('todo', 'on_progress', 'done')),

                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                       FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ==========================================================
-- DUMMY SEED DATA
-- ==========================================================

INSERT INTO users ( name, email, password_hash)
VALUES
    ( 'Alex Johnson', 'alex@zenith.com', 'secret'),
    ( 'Sarah Miller', 'sarah@zenith.com', 'secret');

INSERT INTO organizations ( name)
VALUES
    ( 'Innovate Inc.');

INSERT INTO organization_members (organization_id, user_id, role)
VALUES
    (1, 1, 'OWNER'),
    (1, 2, 'MEMBER');

INSERT INTO projects ( organization_id, name, description)
VALUES
    (1, 'Phoenix Project', 'A complete overhaul of the main application.');

INSERT INTO tasks (project_id, assignee_id, title, description, due_date, status)
VALUES
    ( 1, 1, 'Implement Login', 'Create login form using JavaFX', '2025-11-30', 'todo'),
    ( 1, 2, 'Fix Database Bug', 'Connection timeout issue', '2025-11-28', 'on_progress');
