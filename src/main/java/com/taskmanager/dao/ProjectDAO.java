package com.taskmanager.dao;

import com.taskmanager.model.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO extends AbstractDAO<Project> {

    @Override
    protected String getTableName() { return "projects"; }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO projects (organization_id, name, description) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdateQuery() {
        return "UPDATE projects SET name = ?, description = ? WHERE id = ?";
    }

    @Override
    protected String getSelectByIdQuery() { return "SELECT * FROM projects WHERE id = ?"; }

    @Override
    protected String getSelectAllQuery() { return "SELECT * FROM projects"; }

    @Override
    protected String getDeleteQuery() { return "DELETE FROM projects WHERE id = ?"; }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Project project) throws SQLException {
        // Parameter Insert
        stmt.setInt(1, project.getOrganizationId());
        stmt.setString(2, project.getName());
        stmt.setString(3, project.getDescription());
    }

    @Override
    protected void setUpdateId(PreparedStatement stmt, Project project) throws SQLException {
        stmt.setInt(4, project.getId());
    }


    @Override
    protected Project mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Project(
                rs.getInt("id"),
                rs.getInt("organization_id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    // --- Custom Queries ---
    public List<Project> findByOrganizationId(int orgId) {
        String sql = "SELECT * FROM projects WHERE organization_id = ?";
        List<Project> list = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, orgId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}