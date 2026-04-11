package de.require4testing.service;

import de.require4testing.model.Task;
import de.require4testing.model.User;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class TaskService {

    public void create(Task task) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            if (task.getUser() != null && task.getUser().getId() > 0) {
                User managedUser = entityManager.getReference(User.class, task.getUser().getId());
                task.setUser(managedUser);
            }

            entityManager.persist(task);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public List<Task> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT t FROM Task t LEFT JOIN FETCH t.user ORDER BY t.id",
                            Task.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public void update(Task task) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            if (task.getUser() != null && task.getUser().getId() > 0) {
                User managedUser = entityManager.getReference(User.class, task.getUser().getId());
                task.setUser(managedUser);
            }

            entityManager.merge(task);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public void updateStatus(Integer taskId, String newStatus) {
        if (taskId == null || newStatus == null || newStatus.isBlank()) {
            return;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Task managedTask = entityManager.find(Task.class, taskId);
            if (managedTask != null) {
                managedTask.setStatus(newStatus);
            }

            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public boolean delete(Integer taskId) {
        if (taskId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Task managedTask = entityManager.find(Task.class, taskId);
            if (managedTask == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            entityManager.remove(managedTask);
            entityManager.getTransaction().commit();
            return true;
        } catch (PersistenceException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return false;
        } finally {
            entityManager.close();
        }
    }
}
