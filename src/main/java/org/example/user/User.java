package org.example.user;

import org.example.bank.BankDetails;
import org.example.db.DBHandler;
import org.example.db.models.CustomerModel;
import org.example.db.models.UserModel;
import org.example.db.util.HibernateUtil;
import org.example.wallet.FiatWallet;
import org.example.wallet.SpotWallet;
import org.hibernate.Session;

import java.util.Date;
import java.util.Optional;

public class User {
    private int userId;
    private String name;
    private Date birthDate;
    private String address;
    private String phone;
    private String email;
    // private SpotWallet spotWallet;
    // private FiatWallet fiatWallet;
    // private BankDetails bankDetails;
    // private List<Transaction> transactions;
    private static int IDs = 1;

    Date accountCreationDate;
    Date lastLoginDate;
    String accountStatus = "active";

    public User() {

    }

    public User(int userId, String name, Date birthDate, String phone, String email,
            Date accountCreationDate, Date lastLoginDate, String accountStatus) {
        this.userId = userId;
        this.name = name;
        this.birthDate = birthDate; // Use the provided birthDate
        this.phone = phone;
        this.email = email;
        this.accountCreationDate = accountCreationDate; // Use the provided accountCreationDate
        this.lastLoginDate = lastLoginDate; // Use the provided lastLoginDate
        this.accountStatus = accountStatus != null ? accountStatus : "active"; // Default to "active" if null
        IDs++; // Increment the static ID counter
    }

    public User(int userId, String name, Date birthDate, String billingAddress, String phone, String email, Date currentDate, Date currentDate1, String active) {
        this.userId = userId;
        this.name = name;
        this.birthDate = birthDate;
        this.address = billingAddress;
        this.phone = phone;
        this.email = email;
        this.accountCreationDate = currentDate;
        this.lastLoginDate = currentDate1;
        this.accountStatus = active != null ? active : "inactive";
    }

    public static int getIDs() {
        return IDs;
    }

    public static void addNewUserDB(User user) {
        DBHandler.saveUser(new UserModel(user.getName(), user.getBirthDate(), user.getAddress(), user.getPhone(), user.getEmail(), user.getAccountCreationDate(), user.getLastLoginDate(), user.getAccountStatus()));
    }

    public int getUserId() {
        return userId;
    }

    public Date getAccountCreationDate() {
        return accountCreationDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getName() {
        return name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static Customer getCustomerByEmail(String email) {
        CustomerModel customerModel = DBHandler.getCustomerByEmail(email);
        if (customerModel == null) {
            return null;
        }
        return new Customer(customerModel.getUser().getUserId(), customerModel.getUser().getName(), customerModel.getUser().getBirthDate(), customerModel.getUser().getAddress(), customerModel.getUser().getPhone(), customerModel.getUser().getEmail(), customerModel.getUser().getAccountCreationDate(), customerModel.getUser().getLastLoginDate(), customerModel.getUser().getAccountStatus(), customerModel.getSpotWallet(), customerModel.getFiatWallet(), customerModel.getBankDetails());
    }

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
