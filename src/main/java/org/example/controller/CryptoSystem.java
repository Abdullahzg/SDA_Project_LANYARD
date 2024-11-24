package org.example.controller;

import org.example.ai.AI;
import org.example.bank.BankDetails;
import org.example.bank.BankDetailsIDGenerator;
import org.example.trans.Transaction;
import org.example.useractions.*;
import org.example.user.Admin;
import org.example.user.Customer;
import org.example.user.User;
import org.example.wallet.FiatWallet;
import org.example.wallet.SpotWallet;
import org.example.wallet.Wallet;
import org.example.wallet.WalletIDGenerator;
import org.example.ai.APIController;
import org.example.currency.Owning;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CryptoSystem {
    private static CryptoSystem instance;
    private List<Customer> customers;
    private List<Admin> admin;
    private Customer loggedInCustomer; // Add logged-in customer
    private Admin loggedInAdmin;
    private final APIController API;
    private final AI ai = new AI();

    private CryptoSystem(String APIS) {
        API = new APIController(APIS);
        customers = new ArrayList<>();
        loggedInCustomer = null;
        loggedInAdmin = null;
        admin = new ArrayList<>();
    }

    public static CryptoSystem getInstance(String APIS) {
        if (instance == null) {
            instance = new CryptoSystem(APIS);
        }
        return instance;
    }

    public void printTopNumber(int i) {
        API.printTopCoins(i);
    }

    public void printSingleCoin(String i) {
        API.printSingleCoin(i);
    }

    JSONArray giveTopCoins(int i){
        return API.giveTopCoins(i);
    }

    void depositToSpotWalletn(float depositAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();

        spotWallet.deposit(depositAmount);
    }

    void withdrawFromSpotWalletn(float withdrawAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();

        spotWallet.withdraw(withdrawAmount);
    }

    void transferBetweenWalletsn(int choice, float transferAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();
        FiatWallet fiatWallet = loggedInCustomer.getFiatWallet();



        // Fetch the exchange rate for USDT to USD
        float exchangeRate = API.getExchangeRate("USDT", "USD");
        if (exchangeRate <= 0) {
            System.out.println("Failed to retrieve the exchange rate. Transfer canceled.");
            return;
        }

        // If transferring from Spot (USD) to Fiat (USDT), invert the exchange rate
        if (choice == 1) {
            exchangeRate = 1 / exchangeRate;
        }

        Wallet sourceWallet = (choice == 1) ? spotWallet : fiatWallet;
        Wallet targetWallet = (choice == 1) ? fiatWallet : spotWallet;


        // Check for insufficient funds in the source wallet
        if (sourceWallet.getBalance() < transferAmount) {
            System.out.println("Insufficient balance. Transfer canceled.");
            return;
        }

        float convertedAmount = transferAmount * exchangeRate;

        sourceWallet.withdraw(transferAmount);
        targetWallet.deposit(convertedAmount);

    }

    public String viewCustomerOwningsn() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }


        return  loggedInCustomer.getFiatWallet().viewOwningsn(API);
    }

    public void viewCustomerTransactions() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }
        loggedInCustomer.displayTransactions();
        loggedInCustomer.applyFilters();
    }

    public void viewTransactionHistory() {
        if (loggedInAdmin == null) {
            System.out.println("No Admin is logged in. Please login first.");
            return;
        }
        Scanner myObj = new Scanner(System.in);
        loggedInAdmin.viewTransaction();

        System.out.println("Do you want to flag transaction for review? (Y/N)");
        String ans = myObj.nextLine();
        if (ans.equalsIgnoreCase("Y")) {
            flagForReview();
        }
    }

    void flagForReview() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter Transaction ID: ");
        String id = myObj.nextLine();
        // if the transaction id is found in the db
        // call the following function
        // transaction.flagForReview_t(transaction);
        // notifyTeam(transaction);
    }

    void notifyTeam(@NotNull Transaction transaction) {
        transaction.notifyTeam(transaction);
        System.out.println("Team has been Notified.");
    }

    public void addNewCustomer(String name, Date birthDate, String phone, String email, String accountStatus,
                               float spotWalletBalance, String currency, float maxBalanceLimit,
                               String cardNumber, Date expiryDate, String bankName, String accountHolderName,
                               String billingAddress, float fiatWalletBalance, List<Owning> fiatOwnings) {
        // Generate unique IDs
        int userId = User.getIDs();
        int spotWalletId = WalletIDGenerator.generateWalletId();
        int fiatWalletId = WalletIDGenerator.generateWalletId();
        int bankDetailsId = BankDetailsIDGenerator.generateDetailsId();

        // Create necessary objects
        Date currentDate = new Date(); // Current date for creation
        SpotWallet spotWallet = new SpotWallet(spotWalletId, spotWalletBalance, currentDate, currency, maxBalanceLimit);
        FiatWallet fiatWallet = new FiatWallet(fiatWalletId, fiatWalletBalance, currentDate, fiatOwnings);
        BankDetails bankDetails = new BankDetails(bankDetailsId, cardNumber, expiryDate, bankName, accountHolderName,
                billingAddress);

        // Create and add new customer
        Customer newCustomer = new Customer(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate,
                accountStatus, spotWallet, fiatWallet, bankDetails);

        User user = new User(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate, "active");
        if(Customer.addNewCustomerDB(user, spotWallet, fiatWallet, bankDetails)) {
            customers.add(newCustomer);
            setLoggedInCustomer(newCustomer);
        }
    }


    public Customer getCustomerByEmail(String email) {
        return Customer.getCustomerByEmail(email);
    }

    public void addNewAdmin(String name, Date birthDate, String address, String phone, String email, String accountStatus) {
        // Generate a unique Admin ID
        int adminId = User.getIDs();

        // Create and add new Admin
        Admin newAdmin = new Admin(adminId, name, birthDate, address, email, phone, new Date(), new Date(), accountStatus);

        if(Admin.addNewAdminDB(newAdmin)) {
            admin.add(newAdmin);
            setLoggedInAdmin(newAdmin);
        }
    }

    public void takeAdminInput() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Ensure strict date parsing

        try {
            // Input personal details
            System.out.print("Enter Admin Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Birth Date (yyyy-MM-dd): ");
            Date birthDate;
            while (true) {
                try {
                    birthDate = dateFormat.parse(scanner.nextLine());
                    break;
                } catch (ParseException e) {
                    System.out.print("Invalid date format. Please enter the birth date in yyyy-MM-dd format: ");
                }
            }

            System.out.print("Enter Address: ");
            String address = scanner.nextLine();

            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine();

            System.out.print("Enter Account Status (active/inactive): ");
            String accountStatus = scanner.nextLine();

            // Call addNewAdmin with gathered inputs
            addNewAdmin(name, birthDate, address, phone, email, accountStatus);
        } catch (Exception e) {
            System.out.println("An error occurred while processing input: " + e.getMessage());
        }
    }

    public void setLoggedInAdmin(Admin loggedInAdmin) {
        this.loggedInAdmin = loggedInAdmin;
    }

    public void takeCustomerInput() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Ensure strict date parsing

        try {
            // Input personal details
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Birth Date (yyyy-MM-dd): ");
            Date birthDate;
            while (true) {
                try {
                    birthDate = dateFormat.parse(scanner.nextLine());
                    break;
                } catch (ParseException e) {
                    System.out.print("Invalid date format. Please enter the birth date in yyyy-MM-dd format: ");
                }
            }

            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine();

            // SpotWallet inputs
            System.out.print("Enter Spot Wallet Balance: ");
            float spotWalletBalance = scanner.nextFloat();

            System.out.print("Enter Currency: ");
            scanner.nextLine(); // Consume leftover newline
            String currency = scanner.nextLine();

            System.out.print("Enter Max Balance Limit: ");
            float maxBalanceLimit = scanner.nextFloat();

            // FiatWallet inputs
            System.out.print("Enter Fiat Wallet Balance: ");
            float fiatWalletBalance = scanner.nextFloat();

            System.out.print("Enter number of Fiat Ownings: ");
            int owningCount = scanner.nextInt();
            scanner.nextLine(); // Consume leftover newline

            List<Owning> fiatOwnings = new ArrayList<>();
            for (int i = 0; i < owningCount; i++) {
                System.out.println("Enter details for Owning #" + (i + 1) + ":");
                System.out.print("Enter Coin Name: ");
                String coin = scanner.nextLine();
                System.out.print("Enter Amount: ");
                float amount = scanner.nextFloat();
                scanner.nextLine(); // Consume leftover newline
                Owning owning = new Owning();
                owning.setCoin(coin);
                owning.setAmount(amount);
                fiatOwnings.add(owning);
            }

            // BankDetails inputs
            System.out.print("Enter Card Number: ");
            String cardNumber = scanner.nextLine();

            System.out.print("Enter Card Expiry Date (yyyy-MM-dd): ");
            Date expiryDate;
            while (true) {
                try {
                    expiryDate = dateFormat.parse(scanner.nextLine());
                    break;
                } catch (ParseException e) {
                    System.out.print("Invalid date format. Please enter the expiry date in yyyy-MM-dd format: ");
                }
            }

            System.out.print("Enter Bank Name: ");
            String bankName = scanner.nextLine();

            System.out.print("Enter Account Holder Name: ");
            String accountHolderName = scanner.nextLine();

            System.out.print("Enter Billing Address: ");
            String billingAddress = scanner.nextLine();

            // Create local instance
            addNewCustomer(name, birthDate, phone, email, "active", spotWalletBalance, currency, maxBalanceLimit,
                    cardNumber, expiryDate, bankName, accountHolderName, billingAddress, fiatWalletBalance,
                    fiatOwnings);

            System.out.println("Customer added successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while processing input: " + e.getMessage());
        }
    }

    public void viewAllCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers available.");
        } else {
            for (Customer customer : customers) {
                System.out.println("\nCustomer ID: " + customer.getUserId());
                System.out.println("Name: " + customer.getName());
                System.out.println("Email: " + customer.getEmail());
                System.out.println("Phone: " + customer.getPhone());
                System.out.println("Account Status: " + customer.getAccountStatus());
                System.out.println("Spot Wallet Balance: " + customer.getSpotWallet().getBalance());
                System.out.println("Fiat Wallet Balance: " + customer.getFiatWallet().getBalance());
                System.out.println("Bank Name: " + customer.getBankDetails().getBankName());
                System.out.println("----------------------------");
            }
        }
    }

    public void setLoggedInCustomer(Customer customer) {
        loggedInCustomer = customer;
        if (customer != null) {
            System.out.println("Logged in as: " + customer.getName() + " (ID: " + customer.getUserId() + ")");
        } else {
            System.out.println("Logged out successfully.");
        }
    }

    public Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public Admin getLoggedInAdmin() {
        return loggedInAdmin;
    }

    List<Customer> getCustomers() {
        return customers;
    }

    public void depositToSpotWallet() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();

        System.out.print("Enter the amount to deposit into the Spot Wallet: ");
        float depositAmount = scanner.nextFloat();
        scanner.nextLine(); // Consume leftover newline

        float newBalance = spotWallet.getBalance() + depositAmount;

        if (newBalance > spotWallet.getMaxBalanceLimit()) {
            System.out.printf("Deposit exceeds the maximum allowed balance of %.2f.\n", spotWallet.getMaxBalanceLimit());
            System.out.printf("Current Spot Wallet Balance: %.2f\n", spotWallet.getBalance());
            System.out.printf("Maximum Deposit Allowed: %.2f\n", spotWallet.getMaxBalanceLimit() - spotWallet.getBalance());
            return;
        }

        System.out.printf("Current Spot Wallet Balance: %.2f\n", spotWallet.getBalance());
        System.out.printf("New Spot Wallet Balance after deposit: %.2f\n", newBalance);

        System.out.print("Confirm deposit? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            spotWallet.deposit(depositAmount);
            spotWallet.depositOrWithdrawDB("Deposit");
        } else {
            System.out.println("Deposit canceled.");
        }
    }

    public void withdrawFromSpotWallet() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the amount to withdraw from the Spot Wallet: ");
        float withdrawAmount = scanner.nextFloat();
        scanner.nextLine(); // Consume leftover newline

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();

        if (spotWallet.getBalance() < withdrawAmount) {
            System.out.printf("Insufficient funds! Current Spot Wallet Balance: %.2f\n", spotWallet.getBalance());
            return;
        }

        float newBalance = spotWallet.getBalance() - withdrawAmount;

        System.out.printf("Current Spot Wallet Balance: %.2f\n", spotWallet.getBalance());
        System.out.printf("New Spot Wallet Balance after withdrawal: %.2f\n", newBalance);

        System.out.print("Confirm withdrawal? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            spotWallet.withdraw(withdrawAmount);
            spotWallet.depositOrWithdrawDB("Withdrawal");
        } else {
            System.out.println("Withdrawal canceled.");
        }
    }

    public void transferBetweenWallets() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();
        FiatWallet fiatWallet = loggedInCustomer.getFiatWallet();

        System.out.println("Select the recipient wallet:");
        System.out.println("1. Transfer from Spot to Fiat Wallet (USD to USDT)");
        System.out.println("2. Transfer from Fiat to Spot Wallet (USDT to USD)");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume leftover newline

        if (choice != 1 && choice != 2) {
            System.out.println("Invalid choice. Transfer canceled.");
            return;
        }

        // Fetch the exchange rate for USDT to USD
        float exchangeRate = API.getExchangeRate("USDT", "USD");
        if (exchangeRate <= 0) {
            System.out.println("Failed to retrieve the exchange rate. Transfer canceled.");
            return;
        }

        // If transferring from Spot (USD) to Fiat (USDT), invert the exchange rate
        if (choice == 1) {
            exchangeRate = 1 / exchangeRate;
        }

        Wallet sourceWallet = (choice == 1) ? spotWallet : fiatWallet;
        Wallet targetWallet = (choice == 1) ? fiatWallet : spotWallet;

        System.out.printf("Current %s Wallet Balance: %.2f\n", (choice == 1) ? "Spot" : "Fiat",
                sourceWallet.getBalance());
        System.out.print("Enter the amount to transfer: ");
        float transferAmount = scanner.nextFloat();
        scanner.nextLine(); // Consume leftover newline

        // Check for insufficient funds in the source wallet
        if (sourceWallet.getBalance() < transferAmount) {
            System.out.println("Insufficient balance. Transfer canceled.");
            return;
        }

        float convertedAmount = transferAmount * exchangeRate;

        System.out.printf("Exchange Rate: 1 USDT = %.2f USD\n", 1 / exchangeRate);
        System.out.printf("Amount after conversion: %.2f %s\n", convertedAmount, (choice == 1) ? "USDT" : "USD");

        System.out.printf("New %s Wallet Balance (after transfer): %.2f\n", (choice == 1) ? "Spot" : "Fiat",
                sourceWallet.getBalance() - transferAmount);
        System.out.printf("New %s Wallet Balance (after transfer): %.2f\n", (choice == 1) ? "Fiat" : "Spot",
                targetWallet.getBalance() + convertedAmount);

        System.out.print("Confirm transfer? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            sourceWallet.withdraw(transferAmount);
            targetWallet.deposit(convertedAmount);

            // Update the database
            if (choice == 1) {
                spotWallet.depositOrWithdrawDB("Withdrawal");
                fiatWallet.depositOrWithdrawDB("Deposit");
            } else {
                fiatWallet.depositOrWithdrawDB("Withdrawal");
                spotWallet.depositOrWithdrawDB("Deposit");
            }

            System.out.println("Transfer successful!");
        } else {
            System.out.println("Transfer canceled.");
        }
    }

    public void buyCoinForLoggedInCustomer() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the code of the coin you want to buy: ");
        String coinCode = scanner.nextLine();

        System.out.print("Enter the amount in USDT you want to spend: ");
        float amount = scanner.nextFloat();

        loggedInCustomer.buyCoin(API, coinCode, amount);
    }

    public void sellCoinForLoggedInCustomer() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }
        loggedInCustomer.getFiatWallet().viewOwnings(API);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the code of the coin you want to sell: ");
        String coinCode = scanner.nextLine();

        System.out.print("Enter the amount in USDT you want to receive: ");
        float usdtAmount = scanner.nextFloat();

        loggedInCustomer.sellCoin(API, coinCode, usdtAmount);
    }

    public void viewCustomerOwnings() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }
        loggedInCustomer.getFiatWallet().viewOwnings(API);
    }

    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message);

    }

    public boolean giveFeedback(int customerid){
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to register a wallet.");
            return false;
        }

        return loggedInCustomer.giveFeedback();
    }

    public void referFriend() {
        Referral refer = new Referral();
        refer.referFriend();
    }

    public void viewPortfolio(int UserID) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to view your portfolio.");
            return;
        }
        int userId = loggedInCustomer.getUserId();
        System.out.printf("Displaying portfolio for logged-in customer: %s (ID: %d)\n",
                loggedInCustomer.getName(), userId);
        System.out.println("========================================");
        viewPortfolio(userId);
    }

    public boolean addComment(int transactionID, String comment) {
        if (loggedInCustomer == null) {
            System.out.print("No customer is logged in. Please log in to perform a transfer.");
            return false;
        }

        Comments comments=new Comments();
        int userid=loggedInCustomer.getUserId();

        return comments.addComment(userid,comment,transactionID);
    }

    public void selectFeedback() {
        if (loggedInAdmin == null) {
            System.out.print("No Admin is logged in. Exiting...");
            return;
        }
        Feedback feedback=new Feedback();
        feedback.selectFeedback();
    }

    public boolean reviewFeedback(int feedbackID,int priority) {
        if (loggedInAdmin == null) {
            System.out.print("No admin is logged in. Exiting...");
        }
        return true;
    }

    public boolean respondDirectly(int feedbackID) {
        if (loggedInAdmin == null) {
            System.out.print("No loggedInAdmin is logged in. Exiting...");
            return false;
        }
        Feedback feedback=new Feedback();
        System.out.print("Enter response: ");
        Scanner sc = new Scanner(System.in);
        String response=sc.nextLine();
        feedback.respondDirectly(feedbackID,response);
        return true;
    }

    public void transferFiatToAnotherUser(Scanner scanner) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform a transfer.");
            return;
        }

        // Take email input for the recipient
        System.out.print("Enter the recipient's email: ");
        String recipientEmail = scanner.nextLine();
        if (recipientEmail.equalsIgnoreCase(loggedInCustomer.getEmail())) {
            System.out.println("You cannot transfer to yourself. Transfer canceled.");
            return;
        }
        Customer recipient = getCustomerByEmail(recipientEmail);
        if (recipient == null) {
            System.out.println("Recipient not found. Transfer canceled.");
            return;
        }

        // Display the user's current balance
        float currentBalance = loggedInCustomer.getFiatWallet().getBalance();
        if (currentBalance <= 0) {
            System.out.println("You have no balance to transfer.");
            return;
        }

        System.out.printf("Your current balance: %.2f\n", currentBalance);

        // Take input for the amount to send
        System.out.print("Enter the amount to send: ");
        float amountToSend = scanner.nextFloat();
        scanner.nextLine(); // Consume leftover newline

        // Validate the amount
        if (amountToSend <= 0 || amountToSend > currentBalance) {
            System.out.println("Invalid amount. Transfer canceled.");
            return;
        }

        // Display how much the user will have left if they send
        float remainingBalance = currentBalance - amountToSend;
        System.out.printf("You will have %.2f left after the transfer.\n", remainingBalance);

        // Confirm the transfer
        System.out.print("Confirm transfer? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("Transfer canceled.");
            return;
        }

        // Perform the transfer
        FiatWallet senderWallet = loggedInCustomer.getFiatWallet();
        FiatWallet recipientWallet = recipient.getFiatWallet();
        senderWallet.transferFiatToAnotherUser(amountToSend, recipientWallet);

        System.out.println("Transfer successful!");

        // Send notification email to recipient
        String emailSubject = "Lanyard | FIAT Transfer Notification";
        String emailBody = "You have received a FIAT transfer of $" + amountToSend + " from " + loggedInCustomer.getName()
                + " (" + loggedInCustomer.getEmail() + ").";

        Email email = new Email(recipient.getEmail(), emailSubject, emailBody);
        email.sendEmail(false);
    }

    public Admin getAdminByEmail(String email) {
        return Admin.getAdminByEmail(email);
    }

    public void askAISuggestions() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to get AI suggestions.");
            return;
        }
    }
}
