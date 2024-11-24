package org.example.sda_frontend.db;

import org.example.sda_frontend.currency.Owning;
import org.example.sda_frontend.db.models.currency.OwningsModel;
import org.example.sda_frontend.db.models.trans.TransactionsModel;
import org.example.sda_frontend.db.models.user.AdminModel;
import org.example.sda_frontend.db.models.user.CustomerModel;
import org.example.sda_frontend.db.models.user.UserModel;
import org.example.sda_frontend.db.models.useractions.FeedbackModel;
import org.example.sda_frontend.db.models.wallet.FiatWalletModel;
import org.example.sda_frontend.db.models.wallet.SpotWalletModel;
import org.example.sda_frontend.db.util.HibernateUtil;
import org.example.sda_frontend.user.Admin;
import org.example.sda_frontend.wallet.FiatWallet;
import org.example.sda_frontend.wallet.SpotWallet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.util.ArrayList;
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

    public static boolean saveCustomer( CustomerModel customer) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(customer.getUser());
            session.merge(customer);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    public static void saveSpotWallet(SpotWalletModel spotWallet,  Session session) {
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

    public static boolean saveOrUpdateOwning(int synthID, Owning existingOwning, String coinCode, float amount, float exchangeRate, int walletId, FiatWallet fiatWallet) {
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
            return buySellFIATWithdrawDB(fiatWallet, amount, "bought");
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return false;
    }

    public static boolean buySellFIATWithdrawDB(FiatWallet fiatWallet, float amount, String type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            FiatWalletModel fiatWalletModel = session.get(FiatWalletModel.class, fiatWallet.getWalletId());
            if (fiatWalletModel != null) {
                // Update the balance in the FiatWalletModel
                fiatWalletModel.setBalance(fiatWallet.getBalance());
                fiatWalletModel.setLastActivityDate(new Date());
                session.merge(fiatWalletModel);
                transaction.commit();
                System.out.printf("Successfully %s %.2f USDT from the Fiat Wallet. New Balance: %.2f USDT\n", type, amount, fiatWallet.getBalance());
                return true;
            } else {
                System.out.println("Fiat Wallet not found in the database.");
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    public static boolean updateOwningAfterSell(Owning selectedOwning, String coinCode, float usdtAmount, float exchangeRate, int walletId, FiatWallet fiatWallet) {
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
            return buySellFIATWithdrawDB(fiatWallet, usdtAmount, "sold");
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
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

    public static void depositOrWithdrawSpotWalletDB( SpotWallet spotWallet, String type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            SpotWalletModel spotWalletModel = session.get(SpotWalletModel.class, spotWallet.getWalletId());
            if (spotWalletModel != null) {
                spotWalletModel.setBalance(spotWallet.getBalance());
                spotWalletModel.setLastActivityDate(new Date());
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

    public static void depositOrWithdrawFiatWalletDB( FiatWallet fiatWallet, String type, boolean isFIATTransfer, double amountToSend) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            FiatWalletModel fiatWalletModel = session.get(FiatWalletModel.class, fiatWallet.getWalletId());
            if (fiatWalletModel != null) {
                fiatWalletModel.setBalance(fiatWallet.getBalance());
                fiatWalletModel.setLastActivityDate(new Date());
                session.merge(fiatWalletModel);
                transaction.commit();
                if (isFIATTransfer) {
                    System.out.printf("Successfully transferred %.2f USDT to the recipient's wallet.\n", amountToSend);
                }
                if (!isFIATTransfer) {
                    System.out.printf("%s successful! New Fiat Wallet Balance: %.2f\n", type, fiatWallet.getBalance());
                }
            } else {
                System.out.println("Fiat Wallet not found in the database.");
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static boolean saveAdmin( Admin newAdmin) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            UserModel userModel = new UserModel(newAdmin.getName(), newAdmin.getBirthDate(), newAdmin.getAddress(), newAdmin.getPhone(), newAdmin.getEmail(), new Date(), new Date(), "active", "admin");
            session.merge(userModel);
            session.merge(new AdminModel(userModel));
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    public static Admin getAdminByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        AdminModel adminModel = null;

        try {
            // Join AdminModel with its associated UserModel and check email
            Query<AdminModel> query = session.createQuery(
                    "FROM AdminModel a WHERE a.user.email = :email",
                    AdminModel.class
            );
            query.setParameter("email", email);
            adminModel = query.uniqueResult();
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return adminModel != null ? new Admin(adminModel.getAdminId(), adminModel.getUser().getName(), adminModel.getUser().getBirthDate(), adminModel.getUser().getAddress(), adminModel.getUser().getEmail(), adminModel.getUser().getPhone(), adminModel.getUser().getAccountCreationDate(), adminModel.getUser().getLastLoginDate(), adminModel.getUser().getStatus()) : null;
    }

    public static boolean insertFeedback(FeedbackModel feedback) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(feedback);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return false;
    }

    public static List<Admin> getAllAdmins() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Admin> admins = null;

        try {
            Query<AdminModel> query = session.createQuery("FROM AdminModel", AdminModel.class);
            List<AdminModel> adminModels = query.list();
            admins = new ArrayList<>();
            for (AdminModel adminModel : adminModels) {
                admins.add(new Admin(adminModel.getAdminId(), adminModel.getUser().getName(), adminModel.getUser().getBirthDate(), adminModel.getUser().getAddress(), adminModel.getUser().getEmail(), adminModel.getUser().getPhone(), adminModel.getUser().getAccountCreationDate(), adminModel.getUser().getLastLoginDate(), adminModel.getUser().getStatus()));
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return admins;
    }
}