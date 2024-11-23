package org.example.bank;

import org.example.db.DBHandler;
import org.example.db.models.BankDetailsModel;
import org.hibernate.Session;

import java.util.Date;

public class BankDetails {
    private int detailsId;
    private String cardNumber;
    private Date expiryDate;
    private String bankName;
    private String accountHolderName;
    private String billingAddress;

    public BankDetails(int detailsId, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress) {
        this.detailsId = detailsId;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.billingAddress = billingAddress;
    }

    public static void addNewBankDetails(int bankDetailsId, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress, Session session) {
        DBHandler.saveBankDetails(new BankDetailsModel(bankDetailsId, cardNumber, expiryDate, bankName, accountHolderName, billingAddress), session);
    }

    public String getBankName() { return bankName; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setBillingAddress(String billingAddress) {this.billingAddress = billingAddress;}
    public void setDetailsId(int detailsId) { this.detailsId = detailsId;}
    public int getDetailsId() { return detailsId; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardNumber() { return cardNumber; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBillingAddress() {return billingAddress;}
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }


}
