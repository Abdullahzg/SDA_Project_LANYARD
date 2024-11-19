package org.example;

import java.util.Date;

class User {
    private int userId;
    private String name;
    private Date birthDate;
    private String address;
    private String phone;
    private String email;
    //private SpotWallet spotWallet;
    //private FiatWallet fiatWallet;
    //private BankDetails bankDetails;
    //private List<Transaction> transactions;
    private static int IDs=1;


    Date accountCreationDate;
    Date lastLoginDate;
    String accountStatus;

    public User(int userId, String name, Date birthDate, String phone, String email,
                Date accountCreationDate, Date lastLoginDate, String accountStatus) {
        this.userId = userId;
        this.name = name;
        this.birthDate = birthDate; // Use the provided birthDate
        this.phone = phone;
        this.email = email;
        this.accountCreationDate = accountCreationDate; // Use the provided accountCreationDate
        this.lastLoginDate = lastLoginDate; // Use the provided lastLoginDate
        this.accountStatus = accountStatus; // Use the provided accountStatus
        IDs++; // Increment the static ID counter
    }

    public static int getIDs(){return IDs;}

    public int getUserId() { return userId; }

    public Date getAccountCreationDate() { return accountCreationDate; }
    public Date getLastLoginDate() { return lastLoginDate; }
    public String getAccountStatus() { return accountStatus; }
    public String getName() { return name; }
    public Date getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setUserId(int userId) {this.userId = userId;}


    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public void setAddress(String address) {
        this.address = address;

    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
