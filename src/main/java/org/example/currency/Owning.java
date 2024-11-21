package org.example.currency;

import java.util.Date;

public class Owning {
    private int owningId;
    private float amount;
    private String coin;

    Date purchaseDate;
    float purchaseRate;
    float profitLoss;

    public Owning(int owningId, float amount, String coin) {
        this.owningId = owningId;
        this.amount = amount;
        this.coin = coin;
    }
    public Owning()
    {

    }
    public int getOwningId() { return owningId; }
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    public String getCoin() { return coin; }

    public void setCoin(String coin) { this.coin = coin; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public float getPurchaseRate() { return purchaseRate; }
    public void setPurchaseRate(float currentValue) { this.purchaseRate = currentValue; }
    public float getProfitLoss() { return profitLoss; }
    public void setProfitLoss(float profitLoss) {
        this.profitLoss = profitLoss;
    }
    public void viewPortfolio(int userId) {
        System.out.printf("Portfolio for Customer ID: %d\n", userId);
        System.out.println("========================================");

        //write the sql query to fetch the fiat wallet details of the user from the db
        //display the details
        //do the same for spot wallet
        System.out.println("========================================");
    }
}
