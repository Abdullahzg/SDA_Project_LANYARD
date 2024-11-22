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
    public boolean =(int userID,String text,String type){
        this.notificationText = text;
        this.notificationType = type;
        this.userid = userID;
        //add this notification to the db with the automatic notification id
        return true;

    }


}
