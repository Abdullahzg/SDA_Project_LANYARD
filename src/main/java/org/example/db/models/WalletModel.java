package org.example.db.models;

import jakarta.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class WalletModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int walletId;

    @Column(nullable = false)
    protected float balance;

    @Column(nullable = false)
    protected Date creationDate;

    protected Date lastActivityDate;
    protected String status;

    public WalletModel() {
    }

    public WalletModel(int spotWalletId, float spotWalletBalance, Date currentDate) {
        this.walletId = spotWalletId;
        this.balance = spotWalletBalance;
        this.creationDate = currentDate;
    }

    // Getters and Setters
    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
