package org.example.sda_frontend.db.models.useractions;

import jakarta.persistence.*;
import org.example.sda_frontend.db.models.user.CustomerModel;
import org.example.sda_frontend.user.Customer;

@Entity
@Table(name = "feedbacks")
public class FeedbackModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int feedbackId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "priority_level")
    private int priorityLevel;

    @Column(name = "status")
    private String status;

    @Column(name = "response")
    private String response;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private CustomerModel customer;

    public FeedbackModel() {
    }

    public FeedbackModel(String subject, String feedback, int priorityLevel, String status, String response, CustomerModel customer) {
        this.subject = subject;
        this.feedback = feedback;
        this.priorityLevel = priorityLevel;
        this.status = status;
        this.response = response;
        this.customer = customer;
    }

    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }
}