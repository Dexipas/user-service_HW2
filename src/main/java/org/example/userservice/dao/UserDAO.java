package org.example.userservice.dao;

import org.example.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDAO {
    User save(User user);
    User update(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    void delete(User user);
    List<User> findAll();
    boolean existsById(UUID id);
    boolean existsByEmail(String email);

}
