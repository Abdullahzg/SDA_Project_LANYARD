package org.example.sda_frontend.user;

import org.example.sda_frontend.db.DBHandler;
import org.example.sda_frontend.db.models.user.CustomerModel;

import java.util.Date;

public class User {
    private int userId;
    private String name;
    private Date birthDate;
    private String address;
    private String phone;
    private String email;
    private static int IDs=1;
    Date accountCreationDate;
    Date lastLoginDate;
    String accountStatus= "active";
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
