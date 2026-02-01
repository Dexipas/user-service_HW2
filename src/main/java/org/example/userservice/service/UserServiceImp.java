package org.example.userservice.service;


import org.example.userservice.dao.UserDAO;
import org.example.userservice.exception.*;
import org.example.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

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

        if(name.matches(regexRu) == name.matches(regexEng)) {
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

    private void validateId(Long id){
        if(id == null) {
            throw new IdNullException(id);
        }
        if(!userDAO.existsById(id)){
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public Long createUser(String name, String email, Integer age) {
        log.info("Начало создания пользователя: email={}", email);
        validateName(name);
        validateEmail(email);
        duplicateEmail(email);
        validateAge(age);
        User user = userDAO.save(new User(null, name, email.toLowerCase(), age, null));
        log.info("Пользователь создан успешно: ID={}, email={}", user.getId(), email);
        return user.getId();
    }

    @Override
    public Long updateInfo(Long id, String name, String email, Integer age) {
        log.info("Начало изменения пользователя: email={}", email);
        validateId(id);
        validateName(name);
        validateEmail(email);
        if(!id.equals(userDAO.findByEmail(email).get().getId()))
            duplicateEmail(email);
        validateAge(age);
        User user = userDAO.update(new User(id, name, email.toLowerCase(), age, null));
        log.info("Пользователь успешно изменен: ID={}, email={}", user.getId(), email);
        return user.getId();
    }

    @Override
    public Optional<User> findById(Long id) {
        log.info("Начало поиска пользователя по id={}", id);
        validateId(id);
        return userDAO.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Начало поиска пользователя по email={}", email);
        validateEmail(email);
        return userDAO.findByEmail(email.toLowerCase());
    }

    @Override
    public boolean deleteUser(Long id) {
        log.info("Начало удаления пользователя: id={}", id);
        validateId(id);
        userDAO.delete(userDAO.findById(id).get());
        boolean delResult = !userDAO.existsById(id);
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
