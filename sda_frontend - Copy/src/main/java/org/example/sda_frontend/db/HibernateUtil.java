package org.example.sda_frontend.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create configuration using DatabaseConfigLoader
                Configuration configuration = DatabaseConfigLoader.configureHibernate();

                // Add annotated classes
                configuration.addAnnotatedClass(org.example.sda_frontend.db.models.User.class);

                // Build SessionFactory
                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                System.err.println("Initial SessionFactory creation failed: " + e.getMessage());
                e.printStackTrace(); // This will print the full stack trace
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}