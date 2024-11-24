package org.example.wallet;

import java.util.Date;

public abstract class Wallet {
    protected int walletId;
    protected float balance;
    protected Date creationDate;
    protected Date lastActivityDate;
    protected String status;

    public Wallet(int walletId, float balance, Date creationDate) {
        this.walletId = walletId;
        this.balance = balance;
        this.creationDate = creationDate;
        this.status = "active";
    }

    public void deposit(float amount) {
        if (status.equals("active")) {
            this.balance += amount;
            this.lastActivityDate = new Date();
        }
    }

    public void withdraw(float amount) {
        if (status.equals("active") && balance >= amount) {
            this.balance -= amount;
            this.lastActivityDate = new Date();
        }
    }

    public int getWalletId() { return walletId; }
    public float getBalance() { return balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public abstract void depositOrWithdrawDB(String type);

}

