package org.example.userservice.dao;

import org.example.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user);
    User update(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void delete(User user);
    List<User> findAll();
    boolean existsById(Long id);
    boolean existsByEmail(String email);

}
