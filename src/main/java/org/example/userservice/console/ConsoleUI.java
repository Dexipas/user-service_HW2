package org.example.userservice.console;

import org.example.userservice.model.User;
import org.example.userservice.service.UserService;
import org.example.userservice.exception.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleUI {
    private UserService userService;
    private static final Logger log = LoggerFactory.getLogger(ConsoleUI.class);
    private static final String ADD_ID_MESSAGE = "Введите id пользователя:";
    private static final String ADD_USER_MESSAGE = "Введите имя пользователя:";
    private static final String ADD_EMAIL_MESSAGE = "Введите email пользователя:";
    private static final String ADD_AGE_MESSAGE = "Введите возраст пользователя:";

    private static final String ERROR_ID_MESSAGE = "Ошибка данных id: ";
    private static final String ERROR_NAME_MESSAGE = "Ошибка данных имени: ";
    private static final String ERROR_EMAIL_MESSAGE = "Ошибка данных email: ";
    private static final String ERROR_AGE_MESSAGE = "Ошибка данных возраста:";
    private static final String ERROR_DUPLICATE_MESSAGE = "Ошибка дубликата email: ";
    private static final String ERROR_UNKNOWN_MESSAGE = "Неизвестная ошибка: ";
    private static final String ERROR_INPUT_MESSAGE = "Ошибка ввода данных";
    private static final int EXIT_NUM = 7;

    private Scanner scanner = new Scanner(System.in);
    private String id;
    private String name;
    private String email;
    private Integer age;

    public ConsoleUI(UserService userService) {
        this.userService = userService;
    }

    public void show() {
        log.info("ConsoleUI запущен.");

        int choice = 0;

        while (choice != EXIT_NUM) {
            String menu = """
                  
                  Меню:
                  1. Добавить пользователя
                  2. Изменить данные пользователя
                  3. Удалить пользователя
                  4. Найти пользователя по id
                  5. Найти пользователя по email
                  6. Вывести всех пользователей
                  7. Выйти
                  """;
            System.out.println(menu);
            try {
                System.out.print("Введите цифру для выбора:");
                choice = (int) scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.printf("%s %s", ERROR_INPUT_MESSAGE, e);
                scanner.next();
                continue;
            }

            switch (choice) {
                case 1 -> addUser();
                case 2 -> changeUserData();
                case 3 -> deleteUser();
                case 4 -> findById();
                case 5 -> findByEmail();
                case 6 -> showAllUsers();
                case 7 -> System.out.println("Закрытие программы...");
                default -> System.out.println("Введите другое цифру");

            }
        }
        log.info("ConsoleUI завершен.");
    }

    private void addUser(){
        System.out.println("__Добавление пользователя__");
        try {
            System.out.print(ADD_USER_MESSAGE);
            name = (String) scanner.next();
            System.out.print(ADD_EMAIL_MESSAGE);
            email = (String) scanner.next();
            System.out.print(ADD_AGE_MESSAGE);
            age = (int) scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.printf("%s %s", ERROR_INPUT_MESSAGE, e);
            scanner.next();
        }
        try {
            UUID userId = userService.createUser(name,email,age);
            System.out.printf("Пользователь с id [%s] добавлен.", userId);
        } catch (InvalidNameException e){
            System.out.printf("%s %s", ERROR_NAME_MESSAGE, e);
        } catch (InvalidEmailException e) {
            System.out.printf("%s %s", ERROR_EMAIL_MESSAGE, e);
        } catch (DuplicateEmailException e){
            System.out.printf("%s %s", ERROR_DUPLICATE_MESSAGE, e);
        } catch (InvalidAgeException e){
            System.out.printf("%s %s", ERROR_AGE_MESSAGE, e);
        } catch (Exception e){
            System.out.printf("%s %s", ERROR_UNKNOWN_MESSAGE, e);
        }
    }

    private void changeUserData(){
        System.out.println("__Изменение данных пользователя__");
        try {
            System.out.print(ADD_ID_MESSAGE);
            id = scanner.next();
            System.out.print(ADD_USER_MESSAGE);
            name = (String) scanner.next();
            System.out.print(ADD_EMAIL_MESSAGE);
            email = (String) scanner.next();
            System.out.print(ADD_AGE_MESSAGE);
            age = (int) scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.printf("%s %s", ERROR_INPUT_MESSAGE, e);
            scanner.next();
        }
        try {
            UUID userId = userService.updateInfo(id,name,email,age);
            System.out.printf("Пользователь с id [%s] изменен.", userId);
        } catch (UserNotFoundException e){
            System.out.printf("%s %s", ERROR_ID_MESSAGE, e);
        } catch (InvalidNameException e){
            System.out.printf("%s %s", ERROR_NAME_MESSAGE, e);
        } catch (InvalidEmailException e) {
            System.out.printf("%s %s", ERROR_EMAIL_MESSAGE, e);
        } catch (DuplicateEmailException e){
            System.out.printf("%s %s", ERROR_DUPLICATE_MESSAGE, e);
        } catch (InvalidAgeException e){
            System.out.printf("%s %s", ERROR_AGE_MESSAGE, e);
        } catch (Exception e){
            System.out.printf("%s %s", ERROR_UNKNOWN_MESSAGE, e);
        }
    }


    private void deleteUser(){
        System.out.println("__Удаление пользователя__");
        try {
            System.out.print(ADD_ID_MESSAGE);
            id = scanner.next();
        } catch (InputMismatchException e) {
            System.out.printf("%s %s", ERROR_INPUT_MESSAGE, e);
            scanner.next();
        }

        try {
            if (userService.deleteUser(id))
                System.out.printf("Пользователь с id [%s] удален.", id);
            else
                System.out.printf("Не удалось удалить пользователя с id [%s].", id);
        } catch (UserNotFoundException e){
            System.out.printf("%s %s", ERROR_ID_MESSAGE, e);
        }

    }

    private void showAllUsers(){
        List<User> users = userService.findAll();
        System.out.println("Список пользователей: ");
        if(users.isEmpty())
            System.out.println("Пусто! Добавьте пользователей");
        users.forEach(System.out::println);
    }

    private void findByEmail() {
        System.out.println("__Поиск пользователя по email__");
        try {
            System.out.print(ADD_EMAIL_MESSAGE);
            email = (String) scanner.next();
        } catch (InputMismatchException e) {
            System.out.printf("%s %s", ERROR_INPUT_MESSAGE, e);
            scanner.next();
        }

        try {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent())
                System.out.printf("Пользователь с email [%s] найден. \n %s", email,user.get());
            else
                System.out.printf("Не удалось найти пользователя с email [%s].", email);
        } catch (EmailEmptyOrNullException e){
            System.out.printf("%s %s", ERROR_EMAIL_MESSAGE, e);
        } catch (Exception e) {
            System.out.printf("%s %s", ERROR_UNKNOWN_MESSAGE, e);
        }
    }

    private void findById() {
        System.out.println("__Поиск пользователя по id__");
        try {
            System.out.print(ADD_ID_MESSAGE);
            id = scanner.next();
        } catch (InputMismatchException e) {
            System.out.printf("%s %s", ERROR_INPUT_MESSAGE, e);
            scanner.next();
        }

        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent())
                System.out.printf("Пользователь с id [%s] найден. \n %s", id,user.get());
            else
                System.out.printf("Не удалось найти пользователя с id [%s].", id);
        } catch (UserNotFoundException e){
            System.out.printf("%s %s", ERROR_ID_MESSAGE, e);
        } catch (Exception e){
            System.out.printf("%s %s", ERROR_UNKNOWN_MESSAGE, e);
        }
    }

}
