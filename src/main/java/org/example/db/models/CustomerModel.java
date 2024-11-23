package org.example.db.models;

import jakarta.persistence.*;
import org.example.bank.BankDetails;
import org.example.user.User;
import org.example.wallet.FiatWallet;
import org.example.wallet.SpotWallet;
import org.example.transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customers")
public class CustomerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private int customerId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserModel user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spot_wallet_id", referencedColumnName = "walletId")
    private SpotWalletModel spotWallet;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fiat_wallet_id", referencedColumnName = "walletId")
    private FiatWalletModel fiatWallet;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_details_id", referencedColumnName = "detailsId")
    private BankDetailsModel bankDetails;

    public CustomerModel() {
        super();
    }

    public CustomerModel(UserModel user, SpotWallet spotWallet, FiatWallet fiatWallet, BankDetails bankDetails) {
        this.user = user;
        this.spotWallet = new SpotWalletModel(spotWallet.getWalletId(), spotWallet.getBalance(),
                spotWallet.getCreationDate(), spotWallet.getCurrency(),
                spotWallet.getMaxBalanceLimit());
        this.fiatWallet = new FiatWalletModel(fiatWallet.getWalletId(), fiatWallet.getBalance(),
                fiatWallet.getCreationDate(), fiatWallet.getOwnings());
        this.bankDetails = new BankDetailsModel(bankDetails.getDetailsId(), bankDetails.getCardNumber(),
                bankDetails.getExpiryDate(), bankDetails.getBankName(),
                bankDetails.getAccountHolderName(), bankDetails.getBillingAddress());
    }

    // Getters and Setters
    public SpotWalletModel getSpotWallet() {
        return spotWallet;
    }

    public void setSpotWallet(SpotWallet spotWallet) {
        this.spotWallet = new SpotWalletModel(spotWallet.getWalletId(), spotWallet.getBalance(), spotWallet.getCreationDate(), spotWallet.getCurrency(), spotWallet.getMaxBalanceLimit());
    }

    public FiatWalletModel getFiatWallet() {
        return fiatWallet;
    }

    public void setFiatWallet(FiatWallet fiatWallet) {
        this.fiatWallet = new FiatWalletModel(fiatWallet.getWalletId(), fiatWallet.getBalance(), fiatWallet.getCreationDate(), fiatWallet.getOwnings());
    }

    public BankDetailsModel getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = new BankDetailsModel(bankDetails.getDetailsId(), bankDetails.getCardNumber(), bankDetails.getExpiryDate(), bankDetails.getBankName(), bankDetails.getAccountHolderName(), bankDetails.getBillingAddress());
    }

    public UserModel getUser() {
        return user;
    }

//    public List<TransactionsModel> getTransactions() {
//        return transactions;
//    }

//    public void setTransactions(List<Transaction> transactions) {
//        this.transactions = transactions;
//    }
}