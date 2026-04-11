package de.require4testing.service;

import de.require4testing.model.Test;
import de.require4testing.model.TestReport;
import de.require4testing.model.User;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class TestReportService {

    public void create(TestReport testReport) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            if (testReport.getTest() != null && testReport.getTest().getId() > 0) {
                Test managedTest = entityManager.getReference(Test.class, testReport.getTest().getId());
                testReport.setTest(managedTest);
            }

            if (testReport.getUser() != null && testReport.getUser().getId() > 0) {
                User managedUser = entityManager.getReference(User.class, testReport.getUser().getId());
                testReport.setUser(managedUser);
            }

            entityManager.persist(testReport);
            markCoveredRequirementsAsDone(entityManager, testReport);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public List<TestReport> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT tr FROM TestReport tr " +
                                    "LEFT JOIN FETCH tr.test " +
                                    "LEFT JOIN FETCH tr.user " +
                                    "ORDER BY tr.id",
                            TestReport.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    public boolean delete(Integer testReportId) {
        if (testReportId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            TestReport managedTestReport = entityManager.find(TestReport.class, testReportId);
            if (managedTestReport == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            entityManager.remove(managedTestReport);
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

    private void markCoveredRequirementsAsDone(EntityManager entityManager, TestReport testReport) {
        if (!"passed".equals(testReport.getStatus()) || testReport.getTest() == null) {
            return;
        }

        List<Integer> requirementIds = entityManager.createQuery(
                        "SELECT DISTINCT tc.requirement.id FROM TestCase tc " +
                                "WHERE tc.test.id = :testId AND tc.requirement IS NOT NULL",
                        Integer.class)
                .setParameter("testId", testReport.getTest().getId())
                .getResultList();

        if (requirementIds.isEmpty()) {
            return;
        }

        entityManager.createQuery(
                        "UPDATE Requirement r SET r.status = :status " +
                                "WHERE r.id IN :requirementIds")
                .setParameter("status", "done")
                .setParameter("requirementIds", requirementIds)
                .executeUpdate();
    }
}
