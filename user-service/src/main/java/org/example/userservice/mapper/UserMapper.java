package org.example.userservice.mapper;

import org.example.userservice.dto.UserDTO;
import org.example.userservice.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserDTO toDTO(User user){
        if(user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    public User toEntity(UserDTO userDTO){
        if(userDTO == null) return null;
        return new User(
                userDTO.id(),
                userDTO.name(),
                userDTO.email(),
                userDTO.age(),
                userDTO.createdAt()
        );
    }

    public List<UserDTO> toList(List<User> users) {
        if(users == null) return null;
        return users.stream()
                .map(this::toDTO)
                .toList();
    }
}
