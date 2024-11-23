package org.example.db;

import org.example.currency.Owning;
import org.example.db.models.*;
import org.example.db.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
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

    public static CustomerModel getCustomerByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CustomerModel customer = null;

        try {
            // Join CustomerModel with its associated UserModel and check email
            Query<CustomerModel> query = session.createQuery(
                    "FROM CustomerModel c WHERE c.user.email = :email",
                    CustomerModel.class
            );
            query.setParameter("email", email);
            customer = query.uniqueResult();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return customer;
    }

    public static void saveOrUpdateOwning(int synthID, Owning existingOwning, String coinCode, float amount, float exchangeRate, int walletId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            if (existingOwning != null) {
                OwningsModel existingOwningModel = new OwningsModel(synthID, existingOwning.getAmount() + (amount / exchangeRate), coinCode, new Date(), walletId);
                session.merge(existingOwningModel);
            } else {
                OwningsModel newOwning = new OwningsModel(synthID, amount / exchangeRate, coinCode, new Date(), walletId);
                newOwning.setPurchaseDate(new Date());
                newOwning.setPurchaseRate(exchangeRate);
                session.merge(newOwning);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}