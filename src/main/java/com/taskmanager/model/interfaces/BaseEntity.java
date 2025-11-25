package com.taskmanager.model.interfaces;

/**
 * Interface marker untuk memastikan semua model yang masuk ke Database
 * memiliki ID. Ini diperlukan untuk Generic DAO.
 */
public interface BaseEntity {
    int getId();
    void setId(int id);
}