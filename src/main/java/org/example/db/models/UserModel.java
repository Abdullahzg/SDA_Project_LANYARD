package org.example.db.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date birthDate;

    private String address;
    private String phone;
    private String email;

    @Column(nullable = false)
    private Date accountCreationDate;

    @Column(nullable = false)
    private Date lastLoginDate;

    @Column(nullable = false)
    private String accountStatus;

    @Column(nullable = false)
    private String userType = "customer";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CustomerModel customer;

    public UserModel() {
    }

    public UserModel(String name, Date birthDate, String billingAddress, String phone, String email, Date currentDate, Date currentDate1, String active) {
        // Remove userId from constructor parameters since it's auto-generated
        this.name = name;
        this.birthDate = birthDate;
        this.address = billingAddress;
        this.phone = phone;
        this.email = email;
        this.accountCreationDate = currentDate;
        this.lastLoginDate = currentDate1;
        this.accountStatus = active != null ? active : "inactive";
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}