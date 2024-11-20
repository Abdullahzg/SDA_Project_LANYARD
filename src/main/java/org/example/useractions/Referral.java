package org.example.useractions;
import java.util.Scanner;

public class Referral {
    public void referFriend()
    {
        if (submitForm()==true)
        {
            System.out.print("Referral is a success");
        }
        else
        {
            System.out.print("Referral failed");
        }
    }
    public boolean submitForm()
    {
        System.out.print("Submit the following details for the form: ");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your ID : ");
        String id = scanner.nextLine();
        //fetch the user's name from the db according to the userID
        //the user ID is incorrect prompt the user that his ID is wrong
//        if !(usernotfound)
//        {
//            System.out.print("User not found");
//            return false;
//        }
        //and ask the user if he wants to quit or enter id again

        System.out.print("Enter your friend's name : ");
        String friend_name = scanner.nextLine();
        System.out.print("Enter your friend's email : ");
        String friend_email = scanner.nextLine();
        if (validateFormat(friend_email)==true)
        {
            System.out.print("Email is correct");
            if (sendReferral(friend_email)==true)
            {
                System.out.print("Email is sent successfully");
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            System.out.print("Email can't be sent. Wrong email entered");
            return false;
        }

    }
    public boolean validateFormat(String email)
    {
        //email being checked if it is in correct format
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(emailRegex);
    }

    public boolean sendReferral(String email)
    {
        System.out.print("Sending email...");
        return true;

    }
}
