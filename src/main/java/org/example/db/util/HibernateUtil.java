package org.example.db.util;

import org.example.db.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create configuration using DatabaseConfigLoader
                Configuration configuration = DatabaseConfigLoader.configureHibernate();

                // Add annotated classes
                configuration.addAnnotatedClass(UserModel.class);
                configuration.addAnnotatedClass(CustomerModel.class);
                configuration.addAnnotatedClass(BankDetailsModel.class);
                configuration.addAnnotatedClass(WalletModel.class);
                configuration.addAnnotatedClass(FiatWalletModel.class);
                configuration.addAnnotatedClass(SpotWalletModel.class);
                configuration.addAnnotatedClass(OwningsModel.class);
                configuration.addAnnotatedClass(TransactionsModel.class);

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