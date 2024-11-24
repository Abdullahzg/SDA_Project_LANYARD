package org.example.db.util;

import org.hibernate.cfg.Configuration;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfigLoader {
    private static final String PROPERTIES_FILE = "lanyard.properties";

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

            // Additional Hibernate configurations
            configuration.setProperty("hibernate.connection.provider_class", "org.hibernate.c3p0.internal.C3P0ConnectionProvider");
            configuration.setProperty("hibernate.c3p0.min_size", "5");
            configuration.setProperty("hibernate.c3p0.max_size", "20");
            configuration.setProperty("hibernate.c3p0.timeout", "300");
            configuration.setProperty("hibernate.c3p0.max_statements", "50");
            configuration.setProperty("hibernate.c3p0.idle_test_period", "300");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.connection.log_sql", "false");

            return configuration;
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }
}