package org.example;

import java.util.Scanner;
import org.example.controller.CryptoSystem;
import org.example.user.Admin;
import org.example.user.Customer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Main {
    public static void main(String[] args) {
        CryptoSystem csMain = CryptoSystem.getInstance("81c671a9-bdd6-4752-b892-76cb9691060c");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Customer");
            System.out.println("2. Admin");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int loginChoice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (loginChoice) {
                case 1:
                    customerUser(csMain, sc);
                    break;

                case 2:
                    adminUser(csMain, sc);
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

    private static void customerUser(CryptoSystem csMain, Scanner sc) {
        handleCustomerMenu(csMain, sc);
    }

    private static @Nullable Customer handleCustomerRegistration(@NotNull CryptoSystem csMain, Scanner sc) {
        System.out.println("\n--- Customer Registration ---");
        csMain.takeCustomerInput();
        Customer loggedInCustomer = csMain.getLoggedInCustomer();

        if (loggedInCustomer != null) {
            System.out.println("Customer registered successfully! Welcome, " + loggedInCustomer.getName());
            return loggedInCustomer;
        } else {
            System.out.println("Customer registration failed.");
            return null;
        }
    }

    private static @Nullable Customer handleCustomerLogin(@NotNull CryptoSystem csMain, @NotNull Scanner sc) {
        System.out.println("\n--- Customer Login ---");
        System.out.print("Enter Customer Email: ");
        String email = sc.nextLine();

        Customer loggedInCustomer = csMain.getCustomerByEmail(email);

        if (loggedInCustomer != null) {
            csMain.setLoggedInCustomer(loggedInCustomer);
            System.out.println("Welcome, " + loggedInCustomer.getName());
            return loggedInCustomer;
        } else {
            System.out.println("Invalid Customer Email.");
            return null;
        }
    }

    private static void handleCustomerMenu(CryptoSystem csMain, @NotNull Scanner sc) {
        Customer loggedInCustomer = null;

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int loginChoice = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (loginChoice == 1) {
                loggedInCustomer = handleCustomerLogin(csMain, sc);
                if (loggedInCustomer == null) {
                    continue;
                }
                break;
            }
            else if (loginChoice == 2) {
                loggedInCustomer = handleCustomerRegistration(csMain, sc);
                if (loggedInCustomer == null) {
                    continue;
                }
                break;
            } else if (loginChoice == 3) {
                    System.out.println("Exiting the system. Goodbye!");
                    sc.close();
                    return;
            } else {
                    System.out.println("Invalid option. Please try again.");
            }
        }

        while (loggedInCustomer != null) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Your Details");
            System.out.println("2. Buy FIAT");
            System.out.println("3. Sell FIAT");
            System.out.println("4. View Owned Coins");
            System.out.println("5. View Your Transactions");
            System.out.println("6. Deposit Funds into Spot Wallet");
            System.out.println("7. Withdraw Funds from Spot Wallet");
            System.out.println("8. Transfer Funds Between Wallets");
            System.out.println("9. Transfer FIAT to Another User");
            System.out.println("10. Display Top Coins");
            System.out.println("11. View Details of Single Coin");
            System.out.println("12. Give Feedback");
            System.out.println("13. Logout");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Customer Details:");
                    System.out.println("Name: " + loggedInCustomer.getName());
                    System.out.println("Email: " + loggedInCustomer.getEmail());
                    System.out.println("Phone: " + loggedInCustomer.getPhone());
                    break;

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
                    System.out.print("Would you like to add a comment? (Y/n)");
                    char ans=sc.next().charAt(0);
                    if (ans == 'Y') {
                        System.out.print("Enter transaction ID");
                        int id=sc.nextInt();
                        System.out.print("Comment: ");
                        String comment=sc.nextLine();
                        if (csMain.addComment(id, comment)) {
                            System.out.println("Comment added.");
                        } else {
                            System.out.println("Comment not added. Something unexpected happened.");
                        }
                        break;
                    } else if (ans == 'n') {
                        break;
                    } else {
                        System.out.print("Wrong input. Exiting the code...");
                    }
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
                    csMain.transferFiatToAnotherUser(sc);
                    break;
                case 10:
                    System.out.print("Enter the number of top coins to display: ");
                    int numCoins = sc.nextInt();
                    sc.nextLine();
                    csMain.printTopNumber(numCoins);
                    break;
                case 11:
                    System.out.print("Enter the code of the coin: ");
                    String coinCode = sc.nextLine();
                    csMain.printSingleCoin(coinCode);
                    break;
                case 12:
                    System.out.print("Feedback: ");

                    if (csMain.giveFeedback(loggedInCustomer.getUserId())) {
                        System.out.println("Feedback submitted successfully!");
                    }
                    else {
                        System.out.println("Feedback could not be submitted!");
                    }
                    break;
                case 13:
                    System.out.println("Logging out...");
                    csMain.setLoggedInCustomer(null);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void adminUser(@NotNull CryptoSystem csMain, Scanner sc) {
        handleAdminMenu(csMain, sc);
    }

    private static void handleAdminMenu(CryptoSystem csMain, @NotNull Scanner sc) {
        Admin loggedInAdmin = null;

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int loginChoice = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (loginChoice == 1) {
                loggedInAdmin = handleAdminLogin(csMain, sc);
                if (loggedInAdmin == null) {
                    continue;
                }
                break;
            }
            else if (loginChoice == 2) {
                loggedInAdmin = handleAdminRegistration(csMain, sc);
                if (loggedInAdmin == null) {
                    continue;
                }
                break;
            } else if (loginChoice == 3) {
                System.out.println("Exiting the system. Goodbye!");
                sc.close();
                return;
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }

        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Customer Details");
            System.out.println("2. Review Transactions");
            System.out.println("3. Manage Feedbacks");
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
                    break;

                case 3:
                    System.out.println("Feedback:");
                    csMain.selectFeedback();
                    System.out.print("Enter feedbackID of the feedback you want to review: ");
                    int feedbackID = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter feedback's priority: ");
                    int priority = sc.nextInt();
                    sc.nextLine();
                    if (csMain.reviewFeedback(feedbackID, priority)) {
                        System.out.println("Feedback reviewed successfully!.");
                        System.out.print("Would you like to respond directly? (Y/n)");
                        char ans=sc.next().charAt(0);
                        if (ans == 'Y') {
                            csMain.respondDirectly(feedbackID);
                        }
                        else if (ans == 'n') {

                        }
                    }
                    else{
                        System.out.println("Feedback could not be reviewed!");
                    }
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

    private static @Nullable Admin handleAdminLogin(@NotNull CryptoSystem csMain, @NotNull Scanner sc) {
        System.out.println("\n--- Admin Login ---");
        System.out.print("Enter Admin Email: ");
        String email = sc.nextLine();

        Admin loggedInAdmin = csMain.getAdminByEmail(email);

        if (loggedInAdmin != null) {
            csMain.setLoggedInAdmin(loggedInAdmin);
            System.out.println("Welcome, " + loggedInAdmin.getName());
            return loggedInAdmin;
        } else {
            System.out.println("Invalid Admin Email.");
            return null;
        }
    }

    private static @Nullable Admin handleAdminRegistration(@NotNull CryptoSystem csMain, @NotNull Scanner sc) {
        System.out.println("\n--- Admin Registration ---");
        csMain.takeAdminInput();
        Admin loggedInAdmin = csMain.getLoggedInAdmin();

        if (loggedInAdmin != null) {
            System.out.println("Admin registered successfully! Welcome, " + loggedInAdmin.getName());
            return loggedInAdmin;
        } else {
            System.out.println("Admin registration failed.");
            return null;
        }
    }
}
