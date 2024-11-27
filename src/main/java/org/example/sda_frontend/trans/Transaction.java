package org.example.sda_frontend.trans;

import org.example.sda_frontend.controller.CryptoSystem;
import org.example.sda_frontend.user.Customer;
import org.example.sda_frontend.user.User;
import org.example.sda_frontend.useractions.Comments;
import java.io.*;
import java.util.*;

import org.example.sda_frontend.db.DBHandler;

import org.example.sda_frontend.db.models.trans.TransactionsModel;

import org.example.sda_frontend.db.models.user.CustomerModel;

public class Transaction {
    int transactionId;
    Customer customer;
    float amount;
    public Date timestamp;
    public String transactionType;  // "buy", "sell"
    public String coin;  // Optional, only used for crypto transactions
    float coinRate;
    boolean sus;
    private List<Comments> comments;

    public Transaction(int transactionId, Customer customer, float amount, Date timestamp, String transactionType, String coin, float coinRate) {
        this.transactionId = transactionId;
        this.customer = customer;
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

    public static List<Transaction> getCustomerSpecificTransactionsFromDB(String customerEmail) {
        // Fetch the transactions from the database
        return DBHandler.getTransactionsByCustomer(customerEmail);
    }

    public String getTransactionComments() {
        List<Comments> comments = Comments.getAllCommentsOnTransaction(this);
        Collections.reverse(comments);
        StringBuilder sb = new StringBuilder();
        for(Comments comment: comments) {
            sb.append(comment.getCustomer().getName()).append(",")
                    .append(comment.getComment()).append(",")
                    .append(timestamp.toString()).append("\n");
        }

        return sb.toString();
    }
    // Getters and setters

    public List<Comments> getCoomments(){
        return comments;
    }

    public void addComment(Customer customer, String commentText, int transactionID) {
        Comments newComment = new Comments(customer, commentText, transactionID);
        comments.add(newComment);
        newComment.saveCommentToDB(customer, this);
    }

    public int getTransactionId() { return transactionId; }

    public Customer getCustomer() { return customer; }

    int generateTransactionId() {
        return (int) (Math.random() * 100000); // Simple random ID generator
    }

    String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().toString(); // Current timestamp
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

    public float getAmount() {
        return amount;
    }

    public float getCoinRate() {
        return coinRate;
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
}
