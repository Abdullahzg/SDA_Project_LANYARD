package org.example;

import java.util.Date;

class Transaction {
    int transactionId;
    User user;
    float amount;
    Date timestamp;
    String transactionType;  // "buy", "sell"
    String coin;  // Optional, only used for crypto transactions
    float coinRate;

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
