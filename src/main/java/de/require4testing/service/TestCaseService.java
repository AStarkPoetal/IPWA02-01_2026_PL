package de.require4testing.service;

import de.require4testing.model.Requirement;
import de.require4testing.model.Test;
import de.require4testing.model.TestCase;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class TestCaseService {

    /**
     * Bei der Speicherung von neu TestCase, der Status von Requirement wird automatische updated.
     */
    public void create(TestCase testCase) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            if (testCase.getRequirement() != null && testCase.getRequirement().getId() > 0) {
                Requirement managedRequirement =
                        entityManager.getReference(Requirement.class, testCase.getRequirement().getId());
                testCase.setRequirement(managedRequirement);
            }

            entityManager.persist(testCase);
            moveRequirementToInProgress(testCase.getRequirement());
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

    /**
     * Nach dem Update wird die Requirement-Statuslogik ebenfalls ausgeführt.
     */
    public void update(TestCase testCase) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            TestCase managedTestCase = entityManager.merge(testCase);
            moveRequirementToInProgress(managedTestCase.getRequirement());
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    /**
     * Verknüpfung eines Testfalls mit einem Test.
     * Wenn dies die erste Zuweisung ist, wechselt der Test vom Status „open“ in „in_progress“.
     */
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
            moveTestToInProgress(managedTest);
            entityManager.getTransaction().commit();
            return true;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    /**
     * Beim Entfernen wird geprüft, ob dem Test noch Testfälle zugeordnet sind. Falls nicht, kann der Test wieder
     * in den Status „open“ zurückgesetzt werden.
     */
    public boolean unassignFromTest(Integer testCaseId) {
        if (testCaseId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            TestCase managedTestCase = entityManager.find(TestCase.class, testCaseId);
            if (managedTestCase == null || managedTestCase.getTest() == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            Test managedTest = managedTestCase.getTest();
            managedTestCase.setTest(null);
            moveTestBackToOpenIfNeeded(entityManager, managedTest);

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

    /**
     * Requirement-Statuslogik: Wenn bereits Testfälle zugeordnet sind, soll der Status nicht „new“ bleiben.
     */
    private void moveRequirementToInProgress(Requirement requirement) {
        if (requirement == null || requirement.getStatus() == null) {
            return;
        }

        if ("new".equals(requirement.getStatus())) {
            requirement.setStatus("in_progress");
        }
    }

    /**
     * Test-Statuslogik: Nach der Zuweisung wechselt der Status von „open“ zu „in_progress“.
     */
    private void moveTestToInProgress(Test test) {
        if (test == null || test.getStatus() == null) {
            return;
        }

        if ("open".equals(test.getStatus())) {
            test.setStatus("in_progress");
        }
    }

    /**
     * Wenn alle Testfälle entfernt wurden, wird der Test wieder in den Status „open“ zurückgesetzt.
     */
    private void moveTestBackToOpenIfNeeded(EntityManager entityManager, Test test) {
        if (test == null || test.getStatus() == null || !"in_progress".equals(test.getStatus())) {
            return;
        }

        Long assignedCount = entityManager.createQuery(
                        "SELECT COUNT(tc) FROM TestCase tc WHERE tc.test.id = :testId",
                        Long.class)
                .setParameter("testId", test.getId())
                .getSingleResult();

        if (assignedCount == 0) {
            test.setStatus("open");
        }
    }
}
