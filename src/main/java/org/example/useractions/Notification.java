package org.example.useractions;

public class Notification {
    int notificationID;
    String notificationType;
    int userid;
    String notificationText;

    public Notification(int notificationID,String notificationType,int user,String notificationText) {
        this.notificationID = notificationID;
        this.notificationType = notificationType;
        this.userid = user;
        this.notificationText = notificationText;
    }

    public Notification() {

    }
}
