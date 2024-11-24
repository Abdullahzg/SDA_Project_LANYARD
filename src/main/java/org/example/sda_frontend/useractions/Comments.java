package org.example.sda_frontend.useractions;

import org.example.sda_frontend.user.User;

public class Comments {
    int commentID;
    User user;
    String comment;
    int transactionID;

    public Comments(User userID, String comment, int tID) {
        this.commentID = 1;
        this.user = userID;
        this.comment = comment;
        this.transactionID=tID;
    }

    public Comments() {
        
    }

    public boolean addComment(int userID,String comment,int transID)
    {
        //save comment in the db with the user id and transaction id
        System.out.println("Adding Comment...");
        if (findUserID(userID))
        {
            if (findTransaction(transID)) {
                //write the sql query to add the comment with the given user id and transID
                return true;
            }
            else {System.out.println("Transaction not found");
            return false;}
        }
        else {
            System.out.println("User not found");
            return false;
        }

    }

    public boolean findUserID(int userID)
    {
        //write the sql query to find the USER BASED ON THE ID
        //if found return true
        //else return false
        return true;
    }

    public boolean addAnonymousComment(int transID, String comment)
    {
        System.out.println("Anonymous comment adding");
        if (findTransaction(transID)) {
            //write the sql query to add the anonymous comment with the given transID
            return true;
        }
        else{
            return false;
        }

    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }
    public void setUserID(User userID) {
        this.user = userID;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getCommentID() {
        return commentID;
    }
    public User getUserID() {
        return user;
    }
    public String getComment() {
        return comment;
    }

    public boolean findTransaction(int transactionID)
    {
        //write the sql query to find the transaction BASED ON THE ID
        //if found return true
        //else return false
        return true;
    }
}
