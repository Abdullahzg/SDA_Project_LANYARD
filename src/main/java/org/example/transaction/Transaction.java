package org.example.transaction;

import org.example.user.User;

import java.util.Date;

public class Transaction {
    int transactionId;
    User user;
    public float amount;
    public Date timestamp;
    public String transactionType;  // "buy", "sell"
    public String coin;  // Optional, only used for crypto transactions
    public float coinRate;

    public Transaction(int transactionId, User user, float amount, Date timestamp, String transactionType, String coin, float coinRate) {
        this.transactionId = transactionId;
        this.user = user;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.coin = coin;
        this.coinRate = coinRate;
    }


    // Getters and setters
    public int getTransactionId() { return transactionId; }
    public User getUser() { return user; }

}
