package com.taskmanager.dao;

import com.taskmanager.model.interfaces.BaseEntity;
import com.taskmanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * FRAMEWORK-LIKE IMPLEMENTATION
 * Kelas ini menangani boilerplate code JDBC (Connection, Statement, ResultSet).
 * Kelas anak hanya perlu mendefinisikan Query dan Mapping.
 */
public abstract class AbstractDAO<T extends BaseEntity> implements GenericDAO<T> {

    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    // --- Abstract Methods ---
    protected abstract String getTableName();
    protected abstract String getInsertQuery();
    protected abstract String getUpdateQuery();
    protected abstract String getSelectByIdQuery();
    protected abstract String getSelectAllQuery();
    protected abstract String getDeleteQuery();

    // Mengisi PreparedStatement untuk Insert/Update
    protected abstract void setStatementParameters(PreparedStatement stmt, T entity) throws SQLException;

    // Mengubah ResultSet menjadi Object Java
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    // --- Generic Implementation ---
    @Override
    public T save(T entity) {
        if (entity.getId() == 0) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private T insert(T entity) {
        try (PreparedStatement stmt = getConnection().prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(stmt, entity);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating entity failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating entity failed, no ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting data into " + getTableName(), e);
        }
    }

    private T update(T entity) {
        try (PreparedStatement stmt = getConnection().prepareStatement(getUpdateQuery())) {
            setStatementParameters(stmt, entity);
            // Biasanya parameter terakhir Update adalah ID
            // Kita asumsikan setStatementParameters mengatur semua field KECUALI ID untuk klausa WHERE
            // Ini limitasi implementasi sederhana, di framework asli lebih kompleks.
            // Untuk solusi ini, kita override logic di UserDAO nanti atau tambahkan parameter ID manual di sini.
            // Agar clean, kita buat method abstract tambahan setUpdateId.
            setUpdateId(stmt, entity);

            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating data in " + getTableName(), e);
        }
    }

    // Helper untuk update ID parameter (biasanya index terakhir)
    protected abstract void setUpdateId(PreparedStatement stmt, T entity) throws SQLException;

    @Override
    public Optional<T> findById(int id) {
        try (PreparedStatement stmt = getConnection().prepareStatement(getSelectByIdQuery())) {
            stmt.setInt(1, id);
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

    @Override
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(getSelectAllQuery())) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(int id) {
        try (PreparedStatement stmt = getConnection().prepareStatement(getDeleteQuery())) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}