package org.example.sda_frontend.useractions;

import org.example.sda_frontend.db.DBHandler;
import org.example.sda_frontend.db.models.trans.TransactionsModel;
import org.example.sda_frontend.db.models.user.CustomerModel;
import org.example.sda_frontend.db.models.useractions.FeedbackModel;
import org.example.sda_frontend.user.Admin;
import org.example.sda_frontend.user.Customer;

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

    public static boolean addFeedbackToDB(Feedback feedbackObj, Customer customer) {
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
