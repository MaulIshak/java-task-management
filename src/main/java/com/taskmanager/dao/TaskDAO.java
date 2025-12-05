package com.taskmanager.dao;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskBuilder;
import com.taskmanager.model.User;
import com.taskmanager.model.enums.TaskStatus;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO extends AbstractDAO<Task> {

    @Override
    protected String getTableName() {
        return "tasks";
    }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO tasks (project_id, assignee_id, title, description, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateQuery() {
        return "UPDATE tasks SET project_id = ?, assignee_id = ?, title = ?, description = ?, due_date = ?, status = ? WHERE id = ?";
    }

    @Override
    protected String getSelectByIdQuery() {
        return "SELECT * FROM tasks WHERE id = ?";
    }

    @Override
    protected String getSelectAllQuery() {
        return "SELECT * FROM tasks";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM tasks WHERE id = ?";
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Task task) throws SQLException {
        stmt.setInt(1, task.getProjectId());

        // Handle Nullable Assignee
        if (task.getAssignee() != null) {
            stmt.setInt(2, task.getAssignee().getId());
        } else {
            stmt.setNull(2, java.sql.Types.INTEGER);
        }

        stmt.setString(3, task.getTitle());
        stmt.setString(4, task.getDescription());

        // Convert Java LocalDate -> SQL Date
        stmt.setDate(5, task.getDueDate() != null ? Date.valueOf(task.getDueDate()) : null);

        // Convert Enum -> String
        stmt.setString(6, task.getStatus().name().toLowerCase());
    }

    @Override
    protected void setUpdateId(PreparedStatement stmt, Task task) throws SQLException {
        stmt.setInt(7, task.getId());
    }

    @Override
    protected Task mapResultSetToEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int projectId = rs.getInt("project_id");
        String title = rs.getString("title");
        String description = rs.getString("description");

        Date sqlDate = rs.getDate("due_date");
        LocalDate dueDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        // Handle Enum
        String statusStr = rs.getString("status");
        // (on progress -> ON_PROGRESS)
        TaskStatus status = TaskStatus.valueOf(statusStr.toUpperCase().replace(" ", "_"));
        // Handle Relasi User (Assignee)
        User assignee = null;
        int assigneeId = rs.getInt("assignee_id");
        if (assigneeId > 0) {
            // Set dummy user terlebih dahulu
            User dummyUser = new User();
            dummyUser.setId(assigneeId);
            assignee = dummyUser;
        }

        return new TaskBuilder(id, projectId, title)
                .setDescription(description)
                .setDueDate(dueDate)
                .setStatus(status)
                .setAssignee(assignee)
                .build();
    }

    // --- Custom Query ---
    public List<Task> findByProjectId(int projectId) {
        String sql = "SELECT * FROM tasks WHERE project_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}