package org.example.db;

import org.example.currency.Owning;
import org.example.db.models.*;
import org.example.db.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DBHandler {
    public static void saveUser(UserModel user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void saveCustomer(CustomerModel customer) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(customer.getUser());
            session.merge(customer);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void saveBankDetails(BankDetailsModel bankDetails, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(bankDetails);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    public static void saveFiatWallet(FiatWalletModel fiatWallet, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(fiatWallet);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    public static void saveSpotWallet(SpotWalletModel spotWallet, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(spotWallet);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    public static void saveOwnings(List<Owning> fiatOwnings, int fiatWalletId, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            for (Owning owning : fiatOwnings) {
                OwningsModel owningsModel = new OwningsModel(owning.getOwningId(), owning.getAmount(), owning.getCoin(), fiatWalletId);
                session.merge(owningsModel);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }
}