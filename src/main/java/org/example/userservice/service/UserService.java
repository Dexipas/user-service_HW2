package org.example.userservice.service;

import org.example.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UUID createUser(String name, String email, Integer age);
    UUID updateInfo(String id, String name, String email, Integer age);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean deleteUser(String id);
    List<User> findAll();
}
