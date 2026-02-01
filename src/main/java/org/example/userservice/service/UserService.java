package org.example.userservice.service;

import org.example.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Long createUser(String name, String email, Integer age);
    Long updateInfo(Long id, String name, String email, Integer age);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean deleteUser(Long id);
    List<User> findAll();
}
