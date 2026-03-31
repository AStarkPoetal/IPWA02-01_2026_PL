package de.require4testing.service;

import de.require4testing.model.User;
import de.require4testing.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

public class UserService {

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

    public User findOrCreateDemoUser() {
        User existingUser = findByEmail("test@test.com");
        if (existingUser != null) {
            return existingUser;
        }

        EntityManager entityManager = JpaUtil.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            User user = new User();
            user.setName("Test User");
            user.setEmail("test@test.com");
            user.setPassword("12345");
            user.setRole("TM");

            entityManager.persist(user);
            entityManager.getTransaction().commit();

            return user;
        } catch (PersistenceException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return findByEmail("test@test.com");
        } finally {
            entityManager.close();
        }
    }
}
