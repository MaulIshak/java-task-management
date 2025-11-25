package com.taskmanager.dao;

import com.taskmanager.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementasi konkret untuk User.
 * Hanya berisi SQL dan Mapping, logika eksekusi ada di AbstractDAO.
 */
public class UserDAO extends AbstractDAO<User> {

    @Override
    protected String getTableName() { return "users"; }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdateQuery() {
        return "UPDATE users SET name = ?, email = ?, password_hash = ? WHERE id = ?";
    }

    @Override
    protected String getSelectByIdQuery() {
        return "SELECT * FROM users WHERE id = ?";
    }

    @Override
    protected String getSelectAllQuery() {
        return "SELECT * FROM users";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM users WHERE id = ?";
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPasswordHash());
    }

    @Override
    protected void setUpdateId(PreparedStatement stmt, User user) throws SQLException {
        stmt.setInt(4, user.getId());
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash")
        );
    }

    // --- Custom Method Spesifik User---
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}