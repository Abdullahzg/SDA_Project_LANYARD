package org.example.useractions;

import org.example.transaction.Transaction;
import org.example.user.User;

public class Comments {
    int commentID;
    int userID;
    String comment;

    public Comments(int commentID, int userID, String comment) {
        this.commentID = commentID;
        this.userID = userID;
        this.comment = comment;
    }

    public void addComment(User user,String comment,Transaction trans)
    {
        //save comment in the db with the user id and transaction id
        System.out.println("Comment");
        System.out.println(comment);
    }

    public void addAnonymousComment(Transaction trans, String comment)
    {
        //save comment in the db with the transaction id
        System.out.println("Anonymous comment");
        System.out.println(comment);
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getCommentID() {
        return commentID;
    }
    public int getUserID() {
        return userID;
    }
    public String getComment() {
        return comment;
    }

}
