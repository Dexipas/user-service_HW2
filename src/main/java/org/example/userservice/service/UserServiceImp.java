package org.example.userservice.service;


import jakarta.transaction.Transactional;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.exception.*;
import org.example.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public UserServiceImp(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    private void validateName(String name) {
        if(name == null || name.isEmpty()){
            throw new NameEmptyOrNullException(name);
        }
        String regexEng = "^[A-Z][a-z]+$";
        String regexRu = "^[А-Я][а-я]+$";

        if(!name.matches(regexRu) && !name.matches(regexEng)) {
            throw new InvalidNameException(name);
        }
    }

    private void validateEmail(String email) {
        if(email == null || email.isEmpty()){
            throw new EmailEmptyOrNullException(email);
        }

        String regexEmail = "^[A-Za-z0-9+_.-]+@([A-Za-z]+)[.][A-Za-z]+$";
        if(!email.matches(regexEmail)) {

            throw new InvalidEmailException(email);
        }


    }

    private void duplicateEmail(String email){
        if(userRepository.existsByEmail(email.toLowerCase())){
            throw new DuplicateEmailException(email);
        }
    }

    private void validateAge(Integer age) {
        if(age == null) {
            throw new AgeNullException(age);
        }
        if((age < 1) || (age > 100)) {
            throw new InvalidAgeException(age);
        }
    }

    private void validateId(String id){
        if(id == null) {
            throw new IdNullException(null);
        }

        String regexId = "^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$";
        if(!id.matches(regexId)){
            throw new InvalidIdException(id);
        }

    }

    @Override
    public UUID createUser(UserDTO userDTO) {
        log.info("Начало создания пользователя: email={}", userDTO.email());
        validateName(userDTO.name());
        validateEmail(userDTO.email());
        duplicateEmail(userDTO.email());
        validateAge(userDTO.age());
        User user = userRepository.save(userMapper.toEntity(userDTO));
        log.info("Пользователь создан успешно: ID={}, email={}", user.getId(), user.getEmail());
        return user.getId();
    }

    @Override
    public UUID updateInfo(UserDTO userDTO) {
        log.info("Начало изменения пользователя: email={}", userDTO.email());
        validateId(userDTO.id().toString());
        if(!userRepository.existsById(userDTO.id())){
            throw new UserNotFoundException(userDTO.id());
        }
        validateName(userDTO.name());
        validateEmail(userDTO.email());
        Optional<User> userChecked = userRepository.findByEmail(userDTO.email());
        if(userChecked.isPresent())
            if(!userDTO.id().equals(userChecked.get().getId()))
                duplicateEmail(userDTO.email());
        validateAge(userDTO.age());
        User user = userRepository.save(userMapper.toEntity(userDTO));
        log.info("Пользователь успешно изменен: ID={}, email={}", user.getId(), user.getEmail());
        return user.getId();
    }

    @Override
    public UserDTO findById(String id) {
        log.info("Начало поиска пользователя по id={}", id);
        validateId(id);
        UUID uuid = UUID.fromString(id);
        Optional<User> userFound = userRepository.findById(uuid);
        return userFound.map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException(uuid));
    }


    @Override
    public boolean deleteUser(String id) {
        log.info("Начало удаления пользователя: id={}", id);
        validateId(id);
        UUID uuid = UUID.fromString(id);
        if(!userRepository.existsById(uuid)){
            throw new UserNotFoundException(uuid);
        }
        Optional<User> user = userRepository.findById(uuid);
        user.ifPresent(userRepository::delete);
        boolean delResult = !userRepository.existsById(uuid);
        if(delResult)
            log.info("Пользователь успешно удален: id={}", id);
        else
            log.info("Пользователь не удален: id={}", id);
        return delResult;
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }


}
