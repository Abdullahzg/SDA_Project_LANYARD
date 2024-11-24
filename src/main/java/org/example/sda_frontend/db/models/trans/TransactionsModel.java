package org.example.sda_frontend.db.models.trans;

import jakarta.persistence.*;
import org.example.sda_frontend.db.models.user.CustomerModel;
import org.example.sda_frontend.db.models.user.UserModel;

import java.util.Date;

@Entity
@Table(name = "transactions")
public class TransactionsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerModel customer;

    private float amount;
    private Date timestamp;
    private String transactionType;  // "buy", "sell"
    private String coin;  // Optional, only used for crypto transactions
    private float coinRate;
    private boolean sus;

    public TransactionsModel() {
    }

    public TransactionsModel(CustomerModel customer, float amount, Date timestamp, String transactionType, String coin, float coinRate, boolean sus) {
        this.customer = customer;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.coin = coin;
        this.coinRate = coinRate;
        this.sus = sus;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public UserModel getUser() {
        return customer.getUser();
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public float getCoinRate() {
        return coinRate;
    }

    public void setCoinRate(float coinRate) {
        this.coinRate = coinRate;
    }

    public boolean isSus() {
        return sus;
    }

    public void setSus(boolean sus) {
        this.sus = sus;
    }
}