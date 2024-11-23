package org.example.db.models;

import jakarta.persistence.*;

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

    @Column(nullable = false)
    private float purchaseRate;

    private float profitLoss;

    public OwningsModel() {
    }

    public OwningsModel(int owningId, float amount, String coin, int fiatWalletId) {
        this.owningId = owningId;
        this.amount = amount;
        this.coin = coin;
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
}