package com.taskmanager.dao;

import com.taskmanager.model.Organization;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementasi konkret untuk Organization.
 * Hanya berisi SQL dan Mapping, logika eksekusi ada di AbstractDAO.
 */
public class OrganizationDAO extends AbstractDAO<Organization> {

    @Override
    protected String getTableName() {   
        return "organizations";
    }

    @Override
    protected String getInsertQuery() {
        // SQL: INSERT INTO organizations (name, code) VALUES (?, ?)
        return "INSERT INTO " + getTableName() + " (name, code) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateQuery() {
        // SQL: UPDATE organizations SET name = ?, code = ? WHERE id = ?
        return "UPDATE " + getTableName() + " SET name = ?, code = ? WHERE id = ?";
    }

    @Override
    protected String getSelectByIdQuery() {
        return "SELECT id, name, code FROM " + getTableName() + " WHERE id = ?";
    }

    @Override
    protected String getSelectAllQuery() {
        return "SELECT id, name, code FROM " + getTableName();
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM " + getTableName() + " WHERE id = ?";
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Organization organization) throws SQLException {
        // Parameter 1: name
        stmt.setString(1, organization.getOrgName());
        // Parameter 2: code
        stmt.setString(2, organization.getCode());
    }

    @Override
    protected void setUpdateId(PreparedStatement stmt, Organization organization) throws SQLException {
        stmt.setInt(3, organization.getId());
    }

    @Override
    protected Organization mapResultSetToEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String code = rs.getString("code");
        Organization organization = new Organization(id, name, code);
        
        return organization;
    }

    // --- Custom Query ---
    /**
     * Mencari organisasi berdasarkan kode unik.
     * Dibutuhkan untuk fitur Join Organization.
     */
    public Optional<Organization> findByCode(String code) {
        String sql = "SELECT id, name, code FROM " + getTableName() + " WHERE code = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, code);
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