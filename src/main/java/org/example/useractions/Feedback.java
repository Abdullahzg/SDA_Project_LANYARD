package org.example.useractions;
import java.util.Scanner;

public class Feedback {
    private static int feedbackCounter = 1;
    int feedbackId;
    String subject;
    String feedback;
    int priority_level;
    boolean status;
    String response;
    int userID;
    private static int generateFeedbackId() {
        return feedbackCounter++;
    }
    public Feedback(String feedback) {
        this.feedback = feedback;
        this.feedbackId = generateFeedbackId();
    }
    public Feedback(int Userid) {
        this.feedbackId = generateFeedbackId();
        this.userID = Userid;
        this.status = false;
        this.response = "";
    }
    public Feedback(String subject,int userID, String feedback, int priority_level) {
        this.feedbackId = generateFeedbackId();
        this.subject = subject;
        this.feedback = feedback;
        this.priority_level = priority_level;
        this.userID = userID;
        this.status = false;
        this.response = "\0";
    }

    public void giveFeedback(int userid) {
        displayForm(userid);
    }

    public boolean displayForm(int userID)
    {

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
        if (validateForm(feedback1,subject1,priorityLevel1)==true){
            //make feedback
            Feedback feedback2 = new Feedback(subject1,userID,feedback1,priority_level);
            if (storeFeedback(feedback)==true)
            {
                displayDetails(feedback2);
                notifyAdmin(feedback2);
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

    public boolean validateForm(String feedback, String subject, int priorityLevel)
    {
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

    public boolean notifyAdmin(Feedback feedback) {
        System.out.println("\nAdmin being Notified...");
        return true;
    }
}
