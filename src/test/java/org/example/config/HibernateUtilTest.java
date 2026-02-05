package org.example.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtilTest {
    public static SessionFactory buildSessionFactory(String url, String name, String pass){
        return new Configuration()
                .configure("hibernate-test.cfg.xml")
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", name)
                .setProperty("hibernate.connection.password", pass)
                .buildSessionFactory();
    }
}
