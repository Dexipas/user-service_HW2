package org.example.userservice.service;


import jakarta.transaction.Transactional;
import org.example.events.UserCreatedEvent;
import org.example.events.UserDeletedEvent;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.exception.*;
import org.example.userservice.model.User;
import org.example.userservice.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImp implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public UserServiceImp(UserRepository userRepository, UserMapper userMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    private void duplicateEmail(String email){
        if(userRepository.existsByEmail(email.toLowerCase())){
            throw new DuplicateEmailException(email);
        }
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Начало создания пользователя: email={}", userDTO.email());
        UserValidation.validateName(userDTO.name());
        UserValidation.validateEmail(userDTO.email());
        duplicateEmail(userDTO.email());
        UserValidation.validateAge(userDTO.age());
        User user = userRepository.save(userMapper.toEntity(userDTO));
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user.getEmail());
        kafkaTemplate.send("user-event", userCreatedEvent);
        log.info("Пользователь создан успешно: ID={}, email={}", user.getId(), user.getEmail());
        return userDTO;
    }

    @Override
    public UserDTO updateInfo(UserDTO userDTO) {
        log.info("Начало изменения пользователя: email={}", userDTO.email());
        UserValidation.validateId(userDTO.id().toString());
        if(!userRepository.existsById(userDTO.id())){
            throw new UserNotFoundException(userDTO.id());
        }
        UserValidation.validateName(userDTO.name());
        UserValidation.validateEmail(userDTO.email());
        Optional<User> userChecked = userRepository.findByEmail(userDTO.email());
        if(userChecked.isPresent())
            if(!userDTO.id().equals(userChecked.get().getId()))
                duplicateEmail(userDTO.email());
        UserValidation.validateAge(userDTO.age());
        User user = userRepository.save(userMapper.toEntity(userDTO));
        log.info("Пользователь успешно изменен: ID={}, email={}", user.getId(), user.getEmail());
        return userDTO;
    }

    @Override
    public UserDTO findById(String id) {
        log.info("Начало поиска пользователя по id={}", id);
        UserValidation.validateId(id);
        UUID uuid = UUID.fromString(id);
        Optional<User> userFound = userRepository.findById(uuid);
        return userFound.map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException(uuid));
    }


    @Override
    public boolean deleteUser(String id) {
        log.info("Начало удаления пользователя: id={}", id);
        UserValidation.validateId(id);
        UUID uuid = UUID.fromString(id);
        if(!userRepository.existsById(uuid)){
            throw new UserNotFoundException(uuid);
        }
        Optional<User> user = userRepository.findById(uuid);
        String email = user.map(User::getEmail)
                .orElse("");
        user.ifPresent(userRepository::delete);
        boolean delResult = !userRepository.existsById(uuid);
        if(delResult) {
            UserDeletedEvent userDeletedEvent = new UserDeletedEvent(email);
            kafkaTemplate.send("user-event", userDeletedEvent);
            log.info("Пользователь успешно удален: id={}", id);
        }
        else
            log.info("Пользователь не удален: id={}", id);
        return delResult;
    }

    @Override
    public List<UserDTO> findAll() {
        return userMapper.toList(userRepository.findAll());
    }


}
