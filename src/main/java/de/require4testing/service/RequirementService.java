package de.require4testing.service;

import de.require4testing.model.Requirement;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RequirementService {

    public void create(Requirement requirement) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(requirement);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public List<Requirement> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery("SELECT r FROM Requirement r ORDER BY r.id", Requirement.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }
}
