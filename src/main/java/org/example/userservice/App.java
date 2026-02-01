package org.example.userservice;

import org.example.userservice.config.HibernateUtil;
import org.example.userservice.console.ConsoleUI;
import org.example.userservice.dao.UserDAO;
import org.example.userservice.dao.UserDAOImpl;
import org.example.userservice.service.UserService;
import org.example.userservice.service.UserServiceImp;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        log.info("Приложение запущено.");
        log.info("Запуск UserDAO");
        UserDAO userDAO = new UserDAOImpl();
        log.info("Запуск UserService");
        UserService userService = new UserServiceImp(userDAO);
        log.info("Запуск ConsoleUI");
        ConsoleUI consoleUI = new ConsoleUI(userService);
        consoleUI.show();
        log.info("Приложение завершено.");
    }
}
