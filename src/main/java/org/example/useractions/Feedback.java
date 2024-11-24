package org.example.useractions;

import org.example.db.DBHandler;
import org.example.db.models.trans.TransactionsModel;
import org.example.db.models.user.CustomerModel;
import org.example.db.models.useractions.FeedbackModel;
import org.example.user.Admin;
import org.example.user.Customer;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class Feedback {
    private static int feedbackCounter = 1;
    private int feedbackId;
    private String subject;
    private String feedback;
    private int priority_level;
    private String status = "Pending";
    private String response = null;
    private int userID;

    public Feedback(int i, String subject, int userId, String feedback, int priorityLevel) {
        this.feedbackId = i;
        this.subject = subject;
        this.userID = userId;
        this.feedback = feedback;
        this.priority_level = priorityLevel;
    }

    public Feedback(int feedbackId, String subject, int userId, String feedback, int priorityLevel, String status, String response) {
        this.feedbackId = feedbackId;
        this.subject = subject;
        this.userID = userId;
        this.feedback = feedback;
        this.priority_level = priorityLevel;
        this.status = status;
        this.response = response;
    }

    public static int generateFeedbackId() {
        return feedbackCounter++;
    }

    public Feedback(String feedback) {
        this.feedback = feedback;
        this.feedbackId = generateFeedbackId();
    }

    public Feedback() {

    }

    public Feedback(int Userid) {
        this.feedbackId = generateFeedbackId();
        this.userID = Userid;
        this.status = "-";
        this.response = "-";
    }

    public Feedback(String subject,int userID, String feedback, int priority_level) {
        this.feedbackId = generateFeedbackId();
        this.subject = subject;
        this.feedback = feedback;
        this.priority_level = priority_level;
        this.userID = userID;
        this.status = "-";
        this.response = "\0";
    }

    public static boolean notifyAdmin(Feedback feedback, int userID) {
        System.out.println("\nAdmin being Notified...");
        //write an sql query to add the notification and the user id with it
        return true;
    }

    public static boolean addFeedbackToDB(Feedback feedbackObj, @NotNull Customer customer) {
        // Fetch the existing CustomerModel from the database
        CustomerModel customerModel = DBHandler.getCustomerByEmail(customer.getEmail());
        if (customerModel == null) {
            throw new IllegalStateException("Customer not found in the database.");
        }

        FeedbackModel feedback = new FeedbackModel(feedbackObj.getSubject(), feedbackObj.getFeedback(), feedbackObj.getPriorityLevel(), feedbackObj.getStatus(), feedbackObj.getResponse(), customerModel);
        if(DBHandler.insertFeedback(feedback)) {
            Email.notifyAdminsOfNewFeedback(feedbackObj, customer.getName());
            return true;
        }
        return false;
    }

    public void selectFeedback(){
        //write an sql query to show all feedbacks

    }

    public boolean reviewFeedback(int feedbackID,int priority) {
        //write an sql query to find the feedback in the feedback table
        //if not found exit while returning false
        //if found
            //phir write a query to change the status of that feedback to "viewed by Admin"
        // if having an error return false

        return true;
    }

    public boolean respondDirectly(int feedbackID,String response) {
        if (insertResponse(feedbackID,response))
        {
            System.out.println("Successfully added response to the feedback");
            return true;
        }
        return false;
    }

    public boolean insertResponse(int FeedbackID,String response) {
        //write an sql query to add the response in the feedback table with the feedback of the id given
        return true;
    }

    public boolean updateFeedbackStatus(int FeedbackID,String response) {
        this.status = "Admin has responded";

        //write a sql query to update the feedback table
//        Notification notification = new Notification();
//        notification.addNotification(this.userID,response,"feedback response from admin");
        return true;

    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public String getSubject() {
        return subject;
    }

    public int getUserID() {
        return userID;
    }

    public String getFeedback() {
        return feedback;
    }

    public int getPriorityLevel() {
        return priority_level;
    }

    public String getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }
}
