package de.require4testing.service;

import de.require4testing.model.Test;
import de.require4testing.model.TestCase;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

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

    public boolean assignToTest(Integer testCaseId, Integer testId) {
        if (testCaseId == null || testId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            TestCase managedTestCase = entityManager.find(TestCase.class, testCaseId);
            if (managedTestCase == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            Test managedTest = entityManager.find(Test.class, testId);
            if (managedTest == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            if (managedTestCase.getTest() != null && managedTestCase.getTest().getId() == managedTest.getId()) {
                entityManager.getTransaction().rollback();
                return false;
            }

            managedTestCase.setTest(managedTest);
            entityManager.getTransaction().commit();
            return true;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public boolean delete(Integer testCaseId) {
        if (testCaseId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            TestCase managedTestCase = entityManager.find(TestCase.class, testCaseId);
            if (managedTestCase == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            entityManager.remove(managedTestCase);
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
