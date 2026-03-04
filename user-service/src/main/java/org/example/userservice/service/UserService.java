package org.example.userservice.service;

import org.example.userservice.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateInfo(UserDTO userDTO);
    UserDTO findById(String id);
    boolean deleteUser(String id);
    List<UserDTO> findAll();
}
