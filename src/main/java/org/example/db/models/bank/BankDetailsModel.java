package org.example.db.models.bank;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "bank_details")
public class BankDetailsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int detailsId;

    @Column(nullable = false)
    private String cardNumber;

    private Date expiryDate;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountHolderName;

    private String billingAddress;

    public BankDetailsModel() {
    }

    public BankDetailsModel(int bankDetailsId, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress) {
        this.detailsId = bankDetailsId;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.billingAddress = billingAddress;
    }

    // Getters and Setters
    public int getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(int detailsId) {
        this.detailsId = detailsId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }
}