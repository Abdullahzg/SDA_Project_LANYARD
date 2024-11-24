package org.example.sda_frontend.db;

import org.hibernate.cfg.Configuration;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfigLoader {
    private static final String PROPERTIES_FILE = "database.properties";

    public static Configuration configureHibernate() {
        Configuration configuration = new Configuration();

        try {
            // Load properties
            Properties properties = new Properties();
            InputStream inputStream = DatabaseConfigLoader.class.getClassLoader()
                    .getResourceAsStream(PROPERTIES_FILE);

            if (inputStream == null) {
                throw new IOException("Properties file not found: " + PROPERTIES_FILE);
            }

            properties.load(inputStream);

            // Explicitly set the properties
            configuration.setProperty("hibernate.connection.driver_class", properties.getProperty("hibernate.connection.driver_class"));
            configuration.setProperty("hibernate.connection.url", properties.getProperty("hibernate.connection.url"));
            configuration.setProperty("hibernate.connection.username", properties.getProperty("hibernate.connection.username"));
            configuration.setProperty("hibernate.connection.password", properties.getProperty("hibernate.connection.password"));
            configuration.setProperty("hibernate.dialect", properties.getProperty("hibernate.dialect"));

            // Additional Hibernate configurations
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", "true");

            return configuration;
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }
}