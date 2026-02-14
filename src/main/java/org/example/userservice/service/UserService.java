package org.example.userservice.service;

import org.example.userservice.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UUID createUser(UserDTO userDTO);
    UUID updateInfo(UserDTO userDTO);
    UserDTO findById(String id);
    boolean deleteUser(String id);
    List<UserDTO> findAll();
}
