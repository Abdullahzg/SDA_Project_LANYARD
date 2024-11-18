package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CryptoSystem csMain = new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
        Scanner sc = new Scanner(System.in);

        while (true) {
            Customer loggedInCustomer = csMain.getLoggedInCustomer();
            System.out.println("\n--- Crypto System Menu ---");
            if (loggedInCustomer != null) {
                System.out.println("Logged in as: " + loggedInCustomer.getName() + " (ID: " + loggedInCustomer.getUserId() + ")");
            } else {
                System.out.println("No customer is logged in.");
            }

            System.out.println("1. Display top coins");
            System.out.println("2. Display details of a single coin");
            System.out.println("3. Add a new customer");
            System.out.println("4. View all customer details");
            System.out.println("5. Change logged-in customer");
            System.out.println("6. Deposit into Spot Wallet");
            System.out.println("7. Withdraw from Spot Wallet");
            System.out.println("8. Transfer between Spot and Fiat Wallet");
            System.out.println("9. Buy Coin");
            System.out.println("10. Sell Coin");
            System.out.println("11. View Owned Coins");
            System.out.println("12. View Transactions");
            System.out.println("13. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume leftover newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the number of top coins to display: ");
                    int numCoins = sc.nextInt();
                    sc.nextLine();
                    csMain.printTopNumber(numCoins);
                    break;

                case 2:
                    System.out.print("Enter the code of the coin: ");
                    String coinCode = sc.nextLine();
                    csMain.printSingleCoin(coinCode);
                    break;

                case 3:
                    System.out.println("Enter details to add a new customer:");
                    csMain.takeCustomerInput();
                    break;

                case 4:
                    System.out.println("Customer Details:");
                    csMain.viewAllCustomers();
                    break;

                case 5:
                    System.out.println("Change logged-in customer:");
                    System.out.println("Available Customers:");
                    csMain.viewAllCustomers();
                    System.out.print("Enter the ID of the customer to log in (or 0 to log out): ");
                    int customerId = sc.nextInt();
                    sc.nextLine();
                    if (customerId == 0) {
                        csMain.setLoggedInCustomer(null);
                    } else {
                        Customer customerToLogIn = null;
                        for (Customer customer : csMain.getCustomers()) {
                            if (customer.getUserId() == customerId) {
                                customerToLogIn = customer;
                                break;
                            }
                        }
                        if (customerToLogIn != null) {
                            csMain.setLoggedInCustomer(customerToLogIn);
                        } else {
                            System.out.println("Invalid customer ID.");
                        }
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
                    csMain.buyCoinForLoggedInCustomer();
                    break;

                case 10:
                    csMain.sellCoinForLoggedInCustomer();
                    break;

                case 11:
                    csMain.viewCustomerOwnings();
                    break;

                case 12:
                    csMain.viewCustomerTransactions();
                    break;

                case 13:
                    System.out.println("Exiting the system. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}


