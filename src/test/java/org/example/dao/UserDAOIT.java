package org.example.dao;

import org.example.config.HibernateUtilTest;
import org.example.userservice.dao.UserDAO;
import org.example.userservice.dao.UserDAOImpl;
import org.example.userservice.exception.DataAccessException;
import org.example.userservice.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDAOIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    static SessionFactory sessionFactory;
    private static final String USER_NAME = "Ded";
    private static final String USER_EMAIL = "dedok@stariy.su";
    private static final int USER_AGE = 87;

    @BeforeAll
    static void init() {


        sessionFactory = HibernateUtilTest.buildSessionFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @AfterAll
    static void close() {
        sessionFactory.close();
    }

    @AfterEach
    void cleanUp() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void save_shouldPersistUser() {
        UserDAO userDAO = new UserDAOImpl(sessionFactory);
        User user = new User(USER_NAME,USER_EMAIL,USER_AGE);

        userDAO.save(user);

        try(Session session = sessionFactory.openSession()) {
            User userSaved = session.find(User.class, user.getId());

            assertNotNull(userSaved);
            assertEquals(USER_NAME, userSaved.getName());
            assertEquals(USER_EMAIL, userSaved.getEmail());
            assertEquals(USER_AGE, userSaved.getAge());
        }
    }

    @Test
    void findById_shouldReturnUser() {
        UserDAO userDAO = new UserDAOImpl(sessionFactory);
        User user = new User(USER_NAME,USER_EMAIL,USER_AGE);
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }

        User found = userDAO.findById(user.getId()).get();

        assertNotNull(found);
        assertEquals(USER_NAME, found.getName());
        assertEquals(USER_EMAIL, found.getEmail());
        assertEquals(USER_AGE, found.getAge());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        UserDAO userDAO = new UserDAOImpl(sessionFactory);
        User user = new User(USER_NAME,USER_EMAIL,USER_AGE);
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }

        User found = userDAO.findByEmail(user.getEmail()).get();

        assertNotNull(found);
        assertEquals(USER_NAME, found.getName());
        assertEquals(USER_EMAIL, found.getEmail());
        assertEquals(USER_AGE, found.getAge());
    }

    @Test
    void delete_shouldRemoveUser(){
        UserDAO userDAO = new UserDAOImpl(sessionFactory);
        User user = new User(USER_NAME,USER_EMAIL,USER_AGE);
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
        UUID id = user.getId();

        userDAO.delete(user);

        try(Session session = sessionFactory.openSession()){
            User userDeleted = session.find(User.class, id);
            assertNull(userDeleted);
        }
    }

    @Test
    void save_shouldFailOnDuplicateEmail(){
        UserDAO userDAO = new UserDAOImpl(sessionFactory);
        User user1 = new User(USER_NAME, USER_EMAIL, USER_AGE);
        User user2 = new User("Molodoy", USER_EMAIL, 23);

        userDAO.save(user1);

        assertThrows(DataAccessException.class, () -> userDAO.save(user2));
    }
}
