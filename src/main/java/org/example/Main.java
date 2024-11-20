package org.example;

import org.example.controller.CryptoSystem;
import org.example.db.DBHandler;
import org.example.user.Admin;
import org.example.user.Customer;

import java.sql.SQLException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
//        testing db connection
        try (var connection = DBHandler.connect()) {
            if (connection != null) {
                System.out.println("Connected to the PostgreSQL database.");
                try (var stmt = connection.createStatement()) {
                    var sql = "CREATE TABLE products (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    name VARCHAR(255) NOT NULL," +
                            "    price DECIMAL(10, 2) NOT NULL" +
                            ");";
                    stmt.executeUpdate(sql);
                    System.out.println("Created Products table.");
                }
            } else {
                System.err.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
//        testing db connection

        CryptoSystem csMain = new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Login Menu ---");
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Admin");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int loginChoice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (loginChoice) {
                case 1:
                    handleCustomerLogin(csMain, sc);
                    break;

                case 2:
                    handleAdminLogin(csMain, sc);
                    break;

                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleCustomerLogin(CryptoSystem csMain, Scanner sc) {
        System.out.println("\n--- Customer Login ---");
        csMain.takeCustomerInput();
        Customer loggedInCustomer = csMain.getLoggedInCustomer();


        if (loggedInCustomer != null) {
            csMain.setLoggedInCustomer(loggedInCustomer);
            System.out.println("Welcome, " + loggedInCustomer.getName());
            handleCustomerMenu(csMain, sc);
        } else {
            System.out.println("Invalid Customer ID111.");
        }
    }

    private static void handleCustomerMenu(CryptoSystem csMain, Scanner sc) {
        Customer loggedInCustomer = csMain.getLoggedInCustomer();
        while (loggedInCustomer != null) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Your Details");
            System.out.println("2. Buy Coin");
            System.out.println("3. Sell Coin");
            System.out.println("4. View Owned Coins");
            System.out.println("5. View Your Transactions");
            System.out.println("6. Deposit Funds");
            System.out.println("7. Withdraw Funds");
            System.out.println("8. Transfer Funds");
            System.out.println("9. Display Top Coins");
            System.out.println("10. View Details of a Single Coin");
            System.out.println("11. Give Feedback");
            System.out.println("12. Register New User");
            System.out.println("13. Refer a Friend");
            System.out.println("14. Logout");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                {   System.out.println("Customer Details:");
                    System.out.println("Name: " + loggedInCustomer.getName());
                    System.out.println("Email: " + loggedInCustomer.getEmail());
                    System.out.println("Phone: " + loggedInCustomer.getPhone());
                    break;
                }
                case 2:
                    csMain.buyCoinForLoggedInCustomer();
                    break;
                case 3:
                    csMain.sellCoinForLoggedInCustomer();
                    break;
                case 4:
                    csMain.viewCustomerOwnings();
                    break;
                case 5:
                    csMain.viewCustomerTransactions();
                    break;
                case 6:
                    csMain.depositToSpotWallet();
                    break;
                case 7:
                    csMain.withdrawFromSpotWallet();
                    break;
                case 8:
                    csMain.transferBetweenWallets();
                    break;
                case 9:
                {
                    System.out.print("Enter the number of top coins to display: ");
                    int numCoins = sc.nextInt();
                    sc.nextLine();
                    csMain.printTopNumber(numCoins);
                    break;
                }
                case 10:
                    System.out.print("Enter the code of the coin: ");
                    String coinCode = sc.nextLine();
                    csMain.printSingleCoin(coinCode);
                    break;

                case 11:
                    System.out.print("Feedback: ");
                    if (csMain.giveFeedback(loggedInCustomer.getUserId()))
                    {
                        System.out.println("Feedback submitted successfully!");
                    }
                    else
                    {
                        System.out.println("Feedback could not be submitted!");
                    }
                    break;

                case 12:
                {
                    System.out.print("Registering new Customer...");
                    if (csMain.register(loggedInCustomer))
                    {
                        System.out.print("Customer successfully registered!");
                    }
                    else
                    {
                        System.out.print("Customer has not been Registered");
                    }
                    break;
                }
                case 13:
                    csMain.referFriend();
                    break;
                case 14:
                    System.out.println("Logging out...");
                    csMain.setLoggedInCustomer(null);
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleAdminLogin(CryptoSystem csMain, Scanner sc) {
        System.out.println("\n--- Admin Login ---");
        csMain.takeAdminInput();
        Admin loggedInAdmin = csMain.getLoggedInAdmin();

        if (loggedInAdmin != null) {
            System.out.println("Welcome, " + loggedInAdmin.getName());
            handleAdminMenu(csMain, sc, loggedInAdmin);
        } else {
            System.out.println("Invalid Admin ID.");
        }
    }

    private static void handleAdminMenu(CryptoSystem csMain, Scanner sc, Admin loggedInAdmin) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Customer Details");
            System.out.println("2. Review Transactions");
            System.out.println("3. View All Feedbacks");
            System.out.println("4. Send Notification");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    csMain.viewAllCustomers();
                    break;

                case 2:
                    csMain.viewTransactionHistory();
                    //loggedInAdmin.viewTransaction();
                    break;

                case 3:
                    System.out.println("Feedback:");
                    //csMain.viewFeedbacks();
                    break;

                case 4:
                    System.out.print("Enter the notification message: ");
                    String message = sc.nextLine();
                    //csMain.sendNotification(message);
                    System.out.println("Notification sent successfully!");
                    break;

                case 5:
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
