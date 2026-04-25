package de.require4testing.service;

import de.require4testing.model.Test;
import de.require4testing.model.User;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class TestService {

    /**
     * Speichern eines neuen Tests.
     * Für die Beziehungen zum Creator und zum zugewiesenen Tester werden verwaltete JPA-Referenzen verwendet.
     */
    public void create(Test test) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            if (test.getCreatedBy() != null && test.getCreatedBy().getId() > 0) {
                User managedUser = entityManager.getReference(User.class, test.getCreatedBy().getId());
                test.setCreatedBy(managedUser);
            }

            if (test.getAssignedTester() != null && test.getAssignedTester().getId() > 0) {
                User managedTester = entityManager.getReference(User.class, test.getAssignedTester().getId());
                test.setAssignedTester(managedTester);
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

    /**
     * Die für die Ansicht benötigten Beziehungen werden vorab geladen, damit beim JSF-Rendering keine Lazy-Loading-Fehler auftreten.
     */
    public List<Test> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT DISTINCT t FROM Test t " +
                                    "LEFT JOIN FETCH t.testCases " +
                                    "LEFT JOIN FETCH t.createdBy " +
                                    "LEFT JOIN FETCH t.assignedTester " +
                                    "ORDER BY " +
                                    "CASE " +
                                    "WHEN t.status = 'open' THEN 0 " +
                                    "WHEN t.status = 'in_progress' THEN 1 " +
                                    "WHEN t.status = 'done' THEN 2 " +
                                    "ELSE 3 END, " +
                                    "t.id",
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

    /**
     * Vollständige Aktualisierung der Testentität.
     */
    public void update(Test test) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            if (test.getCreatedBy() != null && test.getCreatedBy().getId() > 0) {
                User managedUser = entityManager.getReference(User.class, test.getCreatedBy().getId());
                test.setCreatedBy(managedUser);
            }

            if (test.getAssignedTester() != null && test.getAssignedTester().getId() > 0) {
                User managedTester = entityManager.getReference(User.class, test.getAssignedTester().getId());
                test.setAssignedTester(managedTester);
            }

            entityManager.merge(test);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    /**
     * Als zugewiesener Tester darf nur ein Benutzer mit der Rolle „T“ ausgewählt werden.
     */
    public boolean assignTester(Integer testId, Integer testerId) {
        if (testId == null || testerId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Test managedTest = entityManager.find(Test.class, testId);
            if (managedTest == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            User managedTester = entityManager.find(User.class, testerId);
            if (managedTester == null || !"T".equals(managedTester.getRole())) {
                entityManager.getTransaction().rollback();
                return false;
            }

            managedTest.setAssignedTester(managedTester);
            entityManager.getTransaction().commit();
            return true;
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    public boolean delete(Integer testId) {
        if (testId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Test managedTest = entityManager.find(Test.class, testId);
            if (managedTest == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            entityManager.remove(managedTest);
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
