package org.example.db.models.user;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class AdminModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int adminId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserModel user;

    public AdminModel() {
        super();
    }

    public AdminModel(UserModel user) {
        this.user = user;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}