package org.example.wallet;

import org.example.db.DBHandler;
import org.example.db.models.wallet.SpotWalletModel;
import org.hibernate.Session;

import java.util.Date;

public class SpotWallet extends Wallet {
    private String currency;
    private float maxBalanceLimit;

    public SpotWallet(int walletId, float balance, Date creationDate, String currency, float maxBalanceLimit) {
        super(walletId, balance, creationDate);
        this.currency = currency;
        this.maxBalanceLimit = maxBalanceLimit;
    }

    public static void addNewSpotWalletDB(int spotWalletId, float spotWalletBalance, Date currentDate, String currency, float maxBalanceLimit, Session session) {
         DBHandler.saveSpotWallet(new SpotWalletModel(spotWalletId, spotWalletBalance, currentDate, currency, maxBalanceLimit), session);
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public float getMaxBalanceLimit() { return maxBalanceLimit; }
    public void setMaxBalanceLimit(float maxBalanceLimit) { this.maxBalanceLimit = maxBalanceLimit; }
}
