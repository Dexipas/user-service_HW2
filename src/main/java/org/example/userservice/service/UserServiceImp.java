package org.example.userservice.service;


import org.example.userservice.dao.UserDAO;
import org.example.userservice.exception.*;
import org.example.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImp implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);
    private final UserDAO userDAO;
    //    private final String regexEmail = "^[a-z0-9+_.-]+@([a-z]+)[.][a-z]+$";

    public UserServiceImp(UserDAO userDAO) {
        this.userDAO = userDAO;
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
        if(userDAO.existsByEmail(email.toLowerCase())){
            throw new DuplicateEmailException(email);
        }
    }

    private void validateAge(Integer age) {
        if(age == null) {
            throw new AgeNullException(age);
        }
        if((age < 1) || (age > 100)) { // заменить литералы на константы?
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

        UUID uuid = UUID.fromString(id);
        if(!userDAO.existsById(uuid)){
            throw new UserNotFoundException(uuid);
        }
    }

    @Override
    public UUID createUser(String name, String email, Integer age) {
        log.info("Начало создания пользователя: email={}", email);
        validateName(name);
        validateEmail(email);
        duplicateEmail(email);
        validateAge(age);
        User user = userDAO.save(new User(name, email.toLowerCase(), age));
        log.info("Пользователь создан успешно: ID={}, email={}", user.getId(), email);
        return user.getId();
    }

    @Override
    public UUID updateInfo(String id, String name, String email, Integer age) {
        log.info("Начало изменения пользователя: email={}", email);
        validateId(id);
        UUID uuid = UUID.fromString(id);
        validateName(name);
        validateEmail(email);
        Optional<User> userChecked = userDAO.findByEmail(email);
        if(userChecked.isPresent())
            if(!uuid.equals(userChecked.get().getId()))
                duplicateEmail(email);
        validateAge(age);
        User user = userDAO.update(new User(uuid, name, email.toLowerCase(), age));
        log.info("Пользователь успешно изменен: ID={}, email={}", user.getId(), email);
        return user.getId();
    }

    @Override
    public Optional<User> findById(String id) {
        log.info("Начало поиска пользователя по id={}", id);
        validateId(id);
        UUID uuid = UUID.fromString(id);
        return userDAO.findById(uuid);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Начало поиска пользователя по email={}", email);
        validateEmail(email);
        return userDAO.findByEmail(email.toLowerCase());
    }

    @Override
    public boolean deleteUser(String id) {
        log.info("Начало удаления пользователя: id={}", id);
        validateId(id);
        UUID uuid = UUID.fromString(id);
        Optional<User> user = userDAO.findById(uuid);
        user.ifPresent(userDAO::delete);
        boolean delResult = !userDAO.existsById(uuid);
        if(delResult)
            log.info("Пользователь успешно удален: id={}", id);
        else
            log.info("Пользователь не удален: id={}", id);
        return delResult;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

}
