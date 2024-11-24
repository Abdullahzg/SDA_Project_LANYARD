package org.example.useractions;

import org.example.db.DBHandler;
import org.example.user.Admin;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Email {
    String emailSubject;
    String emailBody;
    String receiverEmail;
    final String host = "smtp.gmail.com";
    final String port = "587";

    public Email(String receiverEmail, String emailSubject, String emailBody) {
        this.receiverEmail = receiverEmail;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
    }

    public static void notifyAdminsOfNewFeedback(Feedback feedback, String name) {
        List<Admin> admins = DBHandler.getAllAdmins();
        for (Admin admin : admins) {
            String subject = "Lanyard Admin | New Feedback Received";
            String message = String.format("Dear %s,\n\nA new feedback has been received from customer: %s.\n\nSubject: %s\n\nFeedback: %s\n\nPriority Level: %d\n\nPlease review it at your earliest convenience.\n\nBest regards,\nYour Team",
                    admin.getName(), name, feedback.getSubject(), feedback.getFeedback(), feedback.getPriorityLevel());
            Email email = new Email(admin.getEmail(), subject, message);
            email.sendEmail(true);
        }
    }

    public void sendEmail(boolean sentToAdmin) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("lanyard.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find lanyard.properties");
                return;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        String username = props.getProperty("sender_email");
        String password = props.getProperty("app_password");

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject(emailSubject);
            message.setText(emailBody);

            Transport.send(message);

            if (!sentToAdmin) {
                System.out.println("Notification email to recipient sent successfully!");
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}