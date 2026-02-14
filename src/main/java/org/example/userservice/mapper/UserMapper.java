package org.example.userservice.mapper;

import org.example.userservice.dto.UserDTO;
import org.example.userservice.model.User;
import org.springframework.stereotype.Component;

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

    //Обработчик Optional
}
