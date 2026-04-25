package de.require4testing.service;

import de.require4testing.model.Requirement;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class RequirementService {

    /**
     * Insert-Operation für die Requirement-Entität.
     */
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

    /**
     * Die Requirement-Liste wird immer direkt aus der Datenbank geladen, sodass die UI auch nach einer Aktualisierung
     * den aktuellen Zustand widerspiegelt.
     */
    public List<Requirement> findAll() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery("SELECT r FROM Requirement r ORDER BY r.id", Requirement.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Beim Bearbeiten "merge" aktualisiert den bereits bestehenden Datensatz.
     */
    public void update(Requirement requirement) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(requirement);
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    /**
     * Das Löschen kann aufgrund eines Fremdschlüssel-Fehlers fehlschlagen,
     * daher wird das Ergebnis als Boolean-Wert an das Bean zurückgegeben.
     */
    public boolean delete(Integer requirementId) {
        if (requirementId == null) {
            return false;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Requirement managedRequirement = entityManager.find(Requirement.class, requirementId);
            if (managedRequirement == null) {
                entityManager.getTransaction().rollback();
                return false;
            }

            entityManager.remove(managedRequirement);
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
