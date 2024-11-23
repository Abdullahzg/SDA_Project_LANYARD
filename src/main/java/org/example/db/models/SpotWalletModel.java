package org.example.db.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "spotWallets")
public class SpotWalletModel extends WalletModel {
    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private float maxBalanceLimit;

    public SpotWalletModel() {
    }

    public SpotWalletModel(int spotWalletId, float spotWalletBalance, Date currentDate, String currency, float maxBalanceLimit) {
        super(spotWalletId, spotWalletBalance, currentDate);
        this.currency = currency;
        this.maxBalanceLimit = maxBalanceLimit;
    }

    // Getters and Setters
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getMaxBalanceLimit() {
        return maxBalanceLimit;
    }

    public void setMaxBalanceLimit(float maxBalanceLimit) {
        this.maxBalanceLimit = maxBalanceLimit;
    }
}