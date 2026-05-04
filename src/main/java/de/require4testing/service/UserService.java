package de.require4testing.service;

import de.require4testing.model.User;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UserService {

    /**
     * Abruf des aus der Session geladenen Benutzers anhand seiner ID.
     */
    public User findById(Integer id) {
        if (id == null) {
            return null;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.find(User.class, id);
        } finally {
            entityManager.close();
        }
    }

    /**
     * Hilfsabfrage anhand der E-Mail-Adresse.
     */
    public User findByEmail(String email) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email",
                            User.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            entityManager.close();
        }
    }

    /**
     * Der Login erfolgt derzeit über einen einfachen Vergleich von E-Mail und Passwort.
     */
    public User authenticate(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email AND u.password = :password",
                            User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            entityManager.close();
        }
    }

    /**
     * Für das Tester-Assignment-Dropdown werden nur Benutzer mit der Rolle „T“ zurückgegeben.
     */
    public List<User> findAllTesters() {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            return entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.role = :role ORDER BY u.name, u.id",
                            User.class)
                    .setParameter("role", "T")
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }
}
