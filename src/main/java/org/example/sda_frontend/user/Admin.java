package org.example.sda_frontend.user;

import org.example.sda_frontend.trans.Transaction;

import org.example.sda_frontend.db.DBHandler;
import java.util.Date;
import java.util.List;

public class Admin extends User {
    // Constructor
    public Admin(int AdminId,String name,Date birthDate, String address, String email, String phone,Date accountCreationDate,Date lastLoginDate, String accountStatus) {

        super(AdminId,name, birthDate,address, phone,email, accountCreationDate,lastLoginDate,accountStatus);

    }



    public static boolean addNewAdminDB(Admin newAdmin) {

        return DBHandler.saveAdmin(newAdmin);

    }



    public static Admin getAdminByEmail(String email) {

        return DBHandler.getAdminByEmail(email);

    }
    public void retrieveTransactionHistory() {
        System.out.println("Retrieving transaction history...");
        Transaction transaction = new Transaction(); // Assuming a default constructor in Transaction
        transaction.printTransactionHistory();
    }

    public void viewTransaction() {
        System.out.println("\n--- Viewing Transactions ---");
        List<Transaction> transactions = Transaction.fetchTransactionsFromFile();

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.printf("Transaction ID: %d | User ID: %d | User Name: %s | Type: %s | Coin: %s | Amount: %.2f | Rate: %.2f | Date: %s\n",
                        transaction.getTransactionId(),
                        transaction.getUser().getUserId(),
                        transaction.getUser().getName(),
                        transaction.transactionType,
                        transaction.coin != null ? transaction.coin : "N/A",
                        transaction.getAmount(),
                        transaction.getCoinRate(),
                        transaction.timestamp
                );
            }
        }
    }
    public void selectFeedback() {
        System.out.println("Selecting feedback...");
        // Logic for selecting feedback
    }
    public void flagForReview(Transaction transaction) {
        System.out.println("Flagging transaction for review...");
        transaction.flagForReview_t(transaction);
    }
    public void notifyTeam(Transaction transaction) {
        System.out.println("Notifying team about transaction ID: " + transaction.getTransactionId());
        // Logic for notifying team
    }
    public void reviewFeedback() {
        System.out.println("feedback...");
        // Logic for reviewing feedback
    }


}
