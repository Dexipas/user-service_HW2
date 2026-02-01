package org.example.userservice.dao;


import org.example.userservice.config.HibernateUtil;
import org.example.userservice.exception.DataAccessException;
import org.example.userservice.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final Logger log = (Logger) LoggerFactory.getLogger(UserDAOImpl.class);
    private static final String ERROR_INTEGRITY_DB = "Нарушение целостности данных при обращении к БД";
    private static final String ERROR_CONNECTION_DB = "Ошибка подключения к БД";
    private static final String ERROR_WORK_DB = "Ошибка работы БД";


    @Override
    public User save(User user) {
        log.debug("Сохранение пользователя в БД: {}", user.getId());
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.info("Пользователь сохранен в БД: ID={}, email={}", user.getId(), user.getEmail());
        } catch (ConstraintViolationException e){
            rollback(transaction);
            log.error("{}: {}",ERROR_INTEGRITY_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_INTEGRITY_DB, e);
        } catch (JDBCConnectionException e) {
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            rollback(transaction);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            rollback(transaction);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Обновление данных пользователя в БД: {}", user.getId());
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            log.info("Пользователь изменен в БД: ID={}, email={}", user.getId(), user.getEmail());
        } catch (ConstraintViolationException e){
            rollback(transaction);
            log.error("{}: {}", ERROR_INTEGRITY_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_INTEGRITY_DB, e);
        } catch (JDBCConnectionException e) {
            rollback(transaction);
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            rollback(transaction);
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("Поиск пользователя в БД по id: {}", id);
        User user = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.find(User.class, id);
        } catch (JDBCConnectionException e) {
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        log.debug("Поиск пользователя в БД по id {} завершен.", id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Поиск пользователя в БД по email: {}", email);
        User user = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.createSelectionQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResultOrNull();
        } catch (JDBCConnectionException e) {
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        log.debug("Поиск пользователя в БД по email {} завершен.", email);
        return Optional.ofNullable(user);
    }

    @Override
    public void delete(User user) {
        log.debug("Удаление из БД пользователя: {}", user.getId());
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(user);
            transaction.commit();
            log.info("Пользователь удален из БД: ID={}, email={}", user.getId(), user.getEmail());
        } catch (ConstraintViolationException e){
            rollback(transaction);
            log.error("{}: {}", ERROR_INTEGRITY_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_INTEGRITY_DB, e);
        } catch (JDBCConnectionException e) {
            rollback(transaction);
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            rollback(transaction);
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("Формирование списка пользователей из БД.");
        List<User> users = new ArrayList<>();
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            users.addAll(session.createQuery("FROM User", User.class).list());
        } catch (JDBCConnectionException e) {
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        log.debug("Список пользователей из БД сформирован.");
        return users;
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Поиск присутствия в БД пользователя по id: {}", id);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            if(session.find(User.class, id) != null) {
                log.debug("Пользователь с id {} найден.", id);
                return true;
            }
        } catch (JDBCConnectionException e) {
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        log.debug("Пользователь в БД с id {} не найден.", id);
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Поиск присутствия в БД пользователя по email: {}", email);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createSelectionQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResultOrNull();
            if(user != null) {
                log.debug("Пользователь в БД с email {} найден.", email);
                return true;
            }
        } catch (JDBCConnectionException e) {
            log.error("{}: {}", ERROR_CONNECTION_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_CONNECTION_DB, e);
        } catch (HibernateException e){
            log.error("{}: {}", ERROR_WORK_DB, e.getMessage(), e);
            throw new DataAccessException(ERROR_WORK_DB, e);
        }
        log.debug("Пользователь в БД с email {} не найден.", email);
        return false;
    }


    private void rollback(Transaction transaction){
        if(transaction != null && transaction.isActive())
            transaction.rollback();
    }
}
