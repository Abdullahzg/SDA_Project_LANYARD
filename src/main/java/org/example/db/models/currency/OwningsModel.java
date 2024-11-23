package org.example.db.models.currency;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ownings")
public class OwningsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int owningId;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private String coin;

    private float purchaseRate;

    private Date purchaseDate;

    private float profitLoss;

    @Column(name = "wallet_id", nullable = false)
    private int walletId; // Add this field

    public OwningsModel() {
    }

    public OwningsModel(int owningId, float amount, String coin, Date purchaseDate, int walletId) {
        this.owningId = owningId;
        this.amount = amount;
        this.coin = coin;
        this.purchaseDate = purchaseDate;
        this.walletId = walletId; // Ensure this line is present
    }

    // Getters and Setters
    public int getOwningId() {
        return owningId;
    }

    public void setOwningId(int owningId) {
        this.owningId = owningId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public float getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(float purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public float getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(float profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }
}