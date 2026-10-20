package org.example.dao;

import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.Persistence;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class GenericDAO<T, ID extends Serializable> {
    private final Class<T> entityClass;
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultPU");

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    public T find(ID identifier) {
        EntityManager entityManager = emf.createEntityManager();
        try {
            return entityManager.find(entityClass, identifier);
        } finally {
            entityManager.close();
        }
    }

    public List<T> findAll() {
        EntityManager entityManager = emf.createEntityManager();
        try {
            String jpqlQuery = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return entityManager.createQuery(jpqlQuery, entityClass).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public void create(T targetEntity) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transactionContext = entityManager.getTransaction();
        try {
            transactionContext.begin();
            // Merge prend en charge les entités nouvelles ainsi que la réassociation sécurisée
            // des graphes d'objets contenant des associations détachées (ex: MapsId, collections ManyToMany)
            T managedInstance = entityManager.merge(targetEntity);
            synchroniserIdentifiant(targetEntity, managedInstance);
            transactionContext.commit();
        } catch (RuntimeException runtimeEx) {
            if (transactionContext != null && transactionContext.isActive()) {
                transactionContext.rollback();
            }
            throw runtimeEx;
        } finally {
            entityManager.close();
        }
    }

    public T update(T targetEntity) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transactionContext = entityManager.getTransaction();
        try {
            transactionContext.begin();
            T entiteModifiee = entityManager.merge(targetEntity);
            synchroniserIdentifiant(targetEntity, entiteModifiee);
            transactionContext.commit();
            return entiteModifiee;
        } catch (RuntimeException runtimeEx) {
            if (transactionContext != null && transactionContext.isActive()) {
                transactionContext.rollback();
            }
            throw runtimeEx;
        } finally {
            entityManager.close();
        }
    }

    public void delete(T targetEntity) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transactionContext = entityManager.getTransaction();
        try {
            transactionContext.begin();
            T entiteAttachee = entityManager.contains(targetEntity) ? targetEntity : entityManager.merge(targetEntity);
            entityManager.remove(entiteAttachee);
            transactionContext.commit();
        } catch (RuntimeException runtimeEx) {
            if (transactionContext != null && transactionContext.isActive()) {
                transactionContext.rollback();
            }
            throw runtimeEx;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Propage l'identifiant généré par la base vers l'instance originale de l'entité
     */
    private void synchroniserIdentifiant(T sourceEntity, T managedEntity) {
        if (sourceEntity == null || managedEntity == null) return;
        Class<?> classeCourante = sourceEntity.getClass();
        while (classeCourante != null && classeCourante != Object.class) {
            for (Field champ : classeCourante.getDeclaredFields()) {
                if (champ.isAnnotationPresent(Id.class) || champ.isAnnotationPresent(EmbeddedId.class)) {
                    champ.setAccessible(true);
                    try {
                        Object valeurId = champ.get(managedEntity);
                        if (valeurId != null) {
                            champ.set(sourceEntity, valeurId);
                        }
                    } catch (IllegalAccessException ignored) {
                    }
                    return;
                }
            }
            classeCourante = classeCourante.getSuperclass();
        }
    }
}
