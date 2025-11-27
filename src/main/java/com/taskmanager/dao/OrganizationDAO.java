package com.taskmanager.dao;

import com.taskmanager.model.Organization;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        // SQL: INSERT INTO organizations (name) VALUES (?)
        return "INSERT INTO " + getTableName() + " (name) VALUES (?)";
    }

    @Override
    protected String getUpdateQuery() {
        // SQL: UPDATE organizations SET name = ? WHERE id = ?
        return "UPDATE " + getTableName() + " SET name = ? WHERE id = ?";
    }

    @Override
    protected String getSelectByIdQuery() {
        return "SELECT id, name FROM " + getTableName() + " WHERE id = ?";
    }

    @Override
    protected String getSelectAllQuery() {
        return "SELECT id, name FROM " + getTableName();
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM " + getTableName() + " WHERE id = ?";
    }

    @Override
    protected void setStatementParameters(PreparedStatement stmt, Organization organization) throws SQLException {
        // Parameter 1: name
        stmt.setString(1, organization.getOrgName());
    }

    @Override
    protected void setUpdateId(PreparedStatement stmt, Organization organization) throws SQLException {
        // Parameter untuk klausa WHERE di Update Query: id
        stmt.setInt(2, organization.getId());
    }

    @Override
    protected Organization mapResultSetToEntity(ResultSet rs) throws SQLException {
        // Karena model Organization yang tersedia tidak memiliki setter selain setId, 
        // kita menggunakan constructor yang ada dan setId dari BaseEntity di AbstractDAO.
        int id = rs.getInt("id");
        String name = rs.getString("name");
        
        Organization organization = new Organization(id, name);
        // Penting: List projects dan members tidak dimuat di DAO ini (Lazy/Eager loading decision)
        
        return organization;
    }
}