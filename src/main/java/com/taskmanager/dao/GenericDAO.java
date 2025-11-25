package com.taskmanager.dao;

import com.taskmanager.model.interfaces.BaseEntity;
import java.util.List;
import java.util.Optional;

// Interface Generic <T>
public interface GenericDAO<T extends BaseEntity> {
    T save(T entity); // Create or Update
    Optional<T> findById(int id);
    List<T> findAll();
    boolean delete(int id);
}