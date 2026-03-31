package de.require4testing.service;

import de.require4testing.model.Test;
import de.require4testing.model.User;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TestService {

    public void create(Test test) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            if (test.getCreatedBy() != null && test.getCreatedBy().getId() > 0) {
                User managedUser = entityManager.getReference(User.class, test.getCreatedBy().getId());
                test.setCreatedBy(managedUser);
            }

            entityManager.persist(test);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public List<Test> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT DISTINCT t FROM Test t " +
                                    "LEFT JOIN FETCH t.testCases " +
                                    "LEFT JOIN FETCH t.createdBy " +
                                    "ORDER BY t.id",
                            Test.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Test findById(Integer id) {
        if (id == null) {
            return null;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.find(Test.class, id);
        } finally {
            entityManager.close();
        }
    }
}
