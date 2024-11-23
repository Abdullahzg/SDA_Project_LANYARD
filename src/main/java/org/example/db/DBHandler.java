package org.example.db;

import org.example.currency.Owning;
import org.example.db.models.bank.BankDetailsModel;
import org.example.db.models.currency.OwningsModel;
import org.example.db.models.trans.TransactionsModel;
import org.example.db.models.user.CustomerModel;
import org.example.db.models.user.UserModel;
import org.example.db.models.wallet.FiatWalletModel;
import org.example.db.models.wallet.SpotWalletModel;
import org.example.db.util.HibernateUtil;
import org.example.wallet.SpotWallet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;

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

            // Fetch the existing owning from the database
            Query<OwningsModel> query = session.createQuery("FROM OwningsModel WHERE walletId = :walletId AND coin = :coinCode", OwningsModel.class);
            query.setParameter("walletId", walletId);
            query.setParameter("coinCode", coinCode);
            OwningsModel existingOwningModel = query.uniqueResult();

            if (existingOwningModel != null) {
                // Update the existing owning
                existingOwningModel.setAmount(existingOwningModel.getAmount() + (amount / exchangeRate));
                existingOwningModel.setPurchaseDate(new Date());
                existingOwningModel.setPurchaseRate(exchangeRate);
                session.merge(existingOwningModel);
            } else {
                // Create a new owning
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

    public static void updateOwningAfterSell(Owning selectedOwning, String coinCode, float usdtAmount, float exchangeRate, int walletId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            if (selectedOwning.getAmount() > 0) {
                // Update the existing owning
                OwningsModel existingOwningModel = new OwningsModel(selectedOwning.getOwningId(), selectedOwning.getAmount(), coinCode, selectedOwning.getPurchaseDate(), walletId);
                existingOwningModel.setPurchaseRate(selectedOwning.getPurchaseRate());
                session.merge(existingOwningModel);
            } else {
                // Remove the owning if the amount is zero
                Query query = session.createQuery("DELETE FROM OwningsModel WHERE owningId = :owningId");
                query.setParameter("owningId", selectedOwning.getOwningId());
                query.executeUpdate();
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void saveTransaction(TransactionsModel transaction) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction dbTransaction = null;
        try {
            dbTransaction = session.beginTransaction();
            session.merge(transaction);
            dbTransaction.commit();
        } catch (HibernateException e) {
            if (dbTransaction != null) dbTransaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void depositOrWithdrawSpotWalletDB(SpotWallet spotWallet, String type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            SpotWalletModel spotWalletModel = session.get(SpotWalletModel.class, spotWallet.getWalletId());
            if (spotWalletModel != null) {
                spotWalletModel.setBalance(spotWallet.getBalance());
                spotWalletModel.setLastActivityDate(spotWallet.getLastActivityDate());
                session.merge(spotWalletModel);
                transaction.commit();
                System.out.printf("%s successful! New Spot Wallet Balance: %.2f\n", type, spotWallet.getBalance());
            } else {
                System.out.println("Spot Wallet not found in the database.");
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}