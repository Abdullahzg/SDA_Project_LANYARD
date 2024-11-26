package org.example.sda_frontend.user;

import org.example.sda_frontend.trans.Transaction;

import org.example.sda_frontend.db.DBHandler;
import java.util.Date;
import java.util.List;

public class Admin extends User {
    // Constructor
    public Admin(int AdminId,String name,Date birthDate, String address, String email, String phone,Date accountCreationDate,Date lastLoginDate, String accountStatus) {
        super(AdminId,name, birthDate,address, phone,email, accountCreationDate,lastLoginDate,accountStatus);
    }

    public static boolean addNewAdminDB(Admin newAdmin) {
        return DBHandler.saveAdmin(newAdmin);
    }

    public static Admin getAdminByEmail(String email) {
        return DBHandler.getAdminByEmail(email);
    }
}
