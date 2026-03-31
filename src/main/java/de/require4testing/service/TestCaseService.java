package de.require4testing.service;

import de.require4testing.model.TestCase;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TestCaseService {

    public void create(TestCase testCase) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(testCase);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public List<TestCase> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT tc FROM TestCase tc " +
                                    "LEFT JOIN FETCH tc.requirement " +
                                    "LEFT JOIN FETCH tc.test " +
                                    "ORDER BY tc.id",
                            TestCase.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public TestCase findById(Integer id) {
        if (id == null) {
            return null;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.find(TestCase.class, id);
        } finally {
            entityManager.close();
        }
    }

    public void update(TestCase testCase) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(testCase);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }
}
