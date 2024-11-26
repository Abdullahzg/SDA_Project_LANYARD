package org.example.sda_frontend.useractions;

import org.example.sda_frontend.db.DBHandler;
import org.example.sda_frontend.trans.Transaction;
import org.example.sda_frontend.user.Customer;

import java.util.List;

public class Comments {
    int commentID;
    Customer customer;
    String comment;
    int transactionID;

    public Comments(Customer customer, String comment, int tID) {
        this.commentID = 1;
        this.customer = customer;
        this.comment = comment;
        this.transactionID=tID;
    }

    public Comments() {
        
    }

    public static List<Comments> getAllCommentsOnTransaction(Transaction transaction) {
        return DBHandler.getCommentsOnSpecificTransaction(transaction.getTransactionId());
    }

    public boolean findCustomerID(int customerID) {
        //write the sql query to find the USER BASED ON THE ID
        //if found return true
        //else return false
        return true;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }
    public void setCustomer(Customer customerID) {
        this.customer = customerID;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getCommentID() {
        return commentID;
    }
    public Customer getCustomer() {
        return customer;
    }
    public String getComment() {
        return comment;
    }

    public void saveCommentToDB(Customer customer, Transaction transaction) {
        DBHandler.saveCommentToDB(customer, transaction, getComment());
    }
}
