package org.example.sda_frontend.trans;

import org.example.sda_frontend.db.models.user.UserModel;
import org.example.sda_frontend.user.Customer;
import org.example.sda_frontend.user.User;
import org.example.sda_frontend.useractions.Comments;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.sda_frontend.db.DBHandler;

import org.example.sda_frontend.db.models.trans.TransactionsModel;

import org.example.sda_frontend.db.models.user.CustomerModel;

public class Transaction {
    int transactionId;
    User user;
    float amount;
    public Date timestamp;
    public String transactionType;  // "buy", "sell"
    public String coin;  // Optional, only used for crypto transactions
    float coinRate;
    boolean sus;
    private List<Comments> comments;
    public static List<Transaction> allTransactions= new ArrayList<>();

    public Transaction(int transactionId, User user, float amount, Date timestamp, String transactionType, String coin, float coinRate) {
        this.transactionId = transactionId;
        this.user = user;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.coin = coin;
        this.coinRate = coinRate;
        sus=false;
        comments=new ArrayList<>();
    }

    public Transaction() {
        sus=false;
    }

    public static List<Transaction> getTransactionsFromDB() {
        // Fetch the transactions from the database
        return DBHandler.getTransactions();
    }

    public static List<Transaction> getUserSpecificTransactionsFromDB(String customerEmail) {
        // Fetch the transactions from the database
        return DBHandler.getTransactionsByCustomer(customerEmail);
    }
    // Getters and setters

    public List<Comments> getCoomments(){
        return comments;
    }

    public void addComment(Comments c){
        comments.add(c);
    }

    public int getTransactionId() { return transactionId; }

    public User getUser() { return user; }

    int generateTransactionId() {
        return (int) (Math.random() * 100000); // Simple random ID generator
    }

    String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().toString(); // Current timestamp
    }

    public void printTransactionHistory() {
        String filePath = "transactions.txt"; // File containing transaction history
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Parse and print the transaction details
                if (parts.length != 5) {
                    System.err.println("Skipping invalid line: " + line);
                    continue;  // Skip malformed lines
                }

                try {
                    // Parse the transaction fields
                    int transactionId = Integer.parseInt(parts[0].trim());
                    int userId = Integer.parseInt(parts[1].trim());
                    String coin = parts[2].trim();
                    float amount = Float.parseFloat(parts[3].trim());
                    long timestamp = Long.parseLong(parts[4].trim());
                    float coinRate = Float.parseFloat(parts[5].trim());

                    // Create the User object
                    User user = new User();
                    user.setUserId(userId);

                    // Create the Transaction object
                    Date timestamp1 = new Date(timestamp);
                    Transaction transaction = new Transaction(transactionId, user, amount, timestamp1, transactionType,coin,coinRate);

                    // Print the transaction details
                    System.out.println("Transaction ID: " + transactionId);
                    System.out.println("User ID: " + userId);
                    if (coin==null ) {
                        System.out.println("Coin: N/A");
                    }
                    else{
                        System.out.println("Coin: "+coin);
                    }
                    System.out.println("Amount: " + amount);
                    System.out.println("Timestamp: " + timestamp1);
                    System.out.println("-----------------------------");

                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid transaction data: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions file: " + e.getMessage());
        }
    }

    public static void saveTransactionToDB(Transaction transaction, Customer customer, float amount, String transactionType, String coin, float coinRate) {
        // Fetch the existing CustomerModel from the database
        CustomerModel customerModel = DBHandler.getCustomerByEmail(customer.getEmail());
        if (customerModel == null) {
            throw new IllegalStateException("Customer not found in the database.");
        }

        // Create a new TransactionsModel instance
        TransactionsModel transactionsModel = new TransactionsModel();
        transactionsModel.setCustomer(customerModel);
        transactionsModel.setAmount(amount);
        transactionsModel.setTimestamp(new Date());
        transactionsModel.setTransactionType(transactionType);
        transactionsModel.setCoin(coin);
        transactionsModel.setCoinRate(coinRate);
        transactionsModel.setSus(transaction.applyFilters_t(transaction));

        // Save the transaction to the database
        DBHandler.saveTransaction(transactionsModel);
    }

    private static String formatTransactionForFile(Transaction transaction) {
        return String.join(",",
                String.valueOf(transaction.transactionId),
                String.valueOf(transaction.user.getUserId()),
                transaction.user.getName(),
                transaction.transactionType,
                transaction.coin != null ? transaction.coin : "N/A",
                String.valueOf(transaction.amount),
                String.valueOf(transaction.coinRate),
                String.valueOf(transaction.timestamp.getTime())
        );
    }

    public static void saveTransactionToFile(Transaction transaction) {
        String filePath = "transactions.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(formatTransactionForFile(transaction));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving transaction to file: " + e.getMessage());
        }
    }

    private static Transaction parseTransactionFromFile(String line) {
        try {
            String[] parts = line.split(",");
            int transactionId = Integer.parseInt(parts[0].trim());
            int userId = Integer.parseInt(parts[1].trim());
            String userName = parts[2].trim();
            String transactionType = parts[3].trim();
            String coin = parts[4].trim().equals("N/A") ? null : parts[4].trim();
            float amount = Float.parseFloat(parts[5].trim());
            float coinRate = Float.parseFloat(parts[6].trim());
            Date timestamp = new Date(Long.parseLong(parts[7].trim()));

            User user = new User(userId, userName, null, null, null, null, null, "active");
            return new Transaction(transactionId, user, amount, timestamp, transactionType, coin, coinRate);
        } catch (Exception e) {
            System.err.println("Error parsing transaction line: " + line);
            return null;
        }
    }

    public static List<Transaction> fetchTransactionsFromFile() {
        String filePath = "transactions.txt";
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = parseTransactionFromFile(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions from file: " + e.getMessage());
        }

        return transactions;
    }

    public boolean applyFilters_t(Transaction transaction) {
        System.out.println("Applying filters to all transactions");
        //yahan par awen ek amount le li hai
        if (transaction.getAmount() > 100000) {
            return true;
        }
        if (transaction.getCoinRate() >50000) {
            return true;
        }
        return false;
    }

    public void flagForReview_t(Transaction transaction) {
        changeStatus(transaction,true);
        System.out.println("Transaction Reviewed");
        //notifyReview team
        System.out.println("Review Team Notified");
    }

    public float getAmount() {
        return amount;
    }

    public float getCoinRate() {
        return coinRate;
    }

    public void notifyTeam(Transaction transaction) {
        System.out.println("Notifying team");
        changeStatus(transaction,true);
        //update the db and notify the compliance team;
        System.out.println("Compliance Team Notified");
    }

    void changeStatus(Transaction transaction,boolean state)
    {
        transaction.sus=state;
    }

    public static List<Transaction> getGlobalTransactionsAsString() {
        List<Transaction> transactions = Transaction.getTransactionsFromDB();
        if (transactions.isEmpty()) {
            return null; // Return an empty string if there are no transactions
        }
        return transactions;
    }

    public static void main(String[] args) {
        // Create a sample Customer object
        Customer customer = Customer.getCustomerByEmail("ayeshaejaz003@gmail.com");

        // Fetch user-specific transactions from the database
        List<Transaction> transactions = getUserSpecificTransactionsFromDB(customer.getEmail());

        // Print the transactions
        if (transactions != null && !transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                System.out.println("Transaction ID: " + transaction.getTransactionId());
                System.out.println("User: " + transaction.getUser().getName());
                System.out.println("Amount: " + transaction.getAmount());
                System.out.println("Timestamp: " + transaction.timestamp);
                System.out.println("Transaction Type: " + transaction.transactionType);
                System.out.println("Coin: " + transaction.coin);
                System.out.println("Coin Rate: " + transaction.getCoinRate());
                System.out.println("-----------------------------");
            }
        } else {
            System.out.println("No transactions found for the customer.");
        }
    }
}
