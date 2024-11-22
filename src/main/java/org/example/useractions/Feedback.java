package org.example.useractions;
import java.util.Scanner;

public class Feedback {
    private static int feedbackCounter = 1;
    int feedbackId;
    String subject;
    String feedback;
    int priority_level;
    String status;
    String response;
    int userID;
    private static int generateFeedbackId() {
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
    public void giveFeedback(int userid) {
        displayForm(userid);
    }
    public boolean displayForm(int userID) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please fill out the feedback form for User ID: " + userID);

        System.out.print("Enter Subject: ");
        String subject1 = scanner.nextLine();

        System.out.print("Enter Feedback: ");
        String feedback1 = scanner.nextLine();
        System.out.print("Enter Priority Level (1-3): ");
        int priorityLevel1 = scanner.nextInt();
        if (priorityLevel1 < 1 || priorityLevel1 > 3)
        {
            System.out.println("Invalid Priority Level");
            System.out.print("Enter Priority Level (1-3): ");
            priorityLevel1 = scanner.nextInt();
        }
        scanner.nextLine();
        if (validateForm(feedback1, subject1, priorityLevel1)){
            //make feedback
            Feedback feedback2 = new Feedback(subject1,userID,feedback1,priority_level);
            if (storeFeedback(feedback))
            {
                displayDetails(feedback2);
                if (notifyAdmin(feedback2,userID))
                {
                    System.out.println("Successfully Notified");
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            System.out.println("Invalid Feedback details");
            return false;
        }


    }
    public void displayDetails(Feedback feedback) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nFeedback Submitted by User ID " + feedback.userID);
        System.out.println("Subject: " + feedback.subject);
        System.out.println("Feedback: " + feedback.feedback);
        System.out.println("Priority Level: " + feedback.priority_level);
        System.out.println("Status: " + feedback.status);
        System.out.println("Response: " + feedback.response);
    }
    public boolean storeFeedback(String feedback) {
        System.out.println("\nFeedback getting stored in database");
        //write query to store in database
        return true;    //if successfully stored in db
        //else return false

    }
    public boolean validateForm(String feedback, String subject, int priorityLevel) {
        System.out.println("\nValidating Feedback details");
        if (subject == null || subject.trim().isEmpty()) {
            System.out.println("Subject cannot be empty.");
            return false;
        }
        if (feedback == null || feedback.trim().isEmpty()) {
            System.out.println("Feedback cannot be empty.");
            return false;
        }
        if (priorityLevel < 1 || priorityLevel > 3) {
            System.out.println("Priority level must be between 1 and 3.");
            return false;
        }
        return true;
    }
    public boolean notifyAdmin(Feedback feedback,int userID) {
        System.out.println("\nAdmin being Notified...");
        //write an sql query to add the notification and the user id with it
        return true;
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
        Notification notification = new Notification();
        notification.addNotification(this.userID,response,"feedback response from admin");
        return true;

    }


}
