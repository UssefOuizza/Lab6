package org.example.service;

import org.example.dao.GenericDAO;
import java.io.Serializable;
import java.util.List;

public class CrudService<T, ID extends Serializable> {
    private final GenericDAO<T, ID> dao;

    public CrudService(Class<T> entityClass) {
        this.dao = new GenericDAO<>(entityClass);
    }

    public CrudService(GenericDAO<T, ID> customDao) {
        this.dao = customDao;
    }

    protected GenericDAO<T, ID> getDao() {
        return dao;
    }

    public T getById(ID identifier) {
        return dao.find(identifier);
    }

    public List<T> getAll() {
        return dao.findAll();
    }

    public void create(T targetEntity) {
        if (targetEntity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        dao.create(targetEntity);
    }

    public T update(T targetEntity) {
        if (targetEntity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        return dao.update(targetEntity);
    }

    public void delete(T targetEntity) {
        if (targetEntity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        dao.delete(targetEntity);
    }
}
