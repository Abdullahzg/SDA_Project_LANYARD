package org.example;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.text.*;
import java.time.Instant;
import java.io.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


abstract class Wallet {
    protected int walletId;
    protected float balance;
    protected Date creationDate;
    protected Date lastActivityDate;
    protected String status;

    public Wallet(int walletId, float balance, Date creationDate) {
        this.walletId = walletId;
        this.balance = balance;
        this.creationDate = creationDate;
        this.status = "active";
    }

    public void deposit(float amount) {
        if (status.equals("active")) {
            this.balance += amount;
            this.lastActivityDate = new Date();
        }
    }

    public void withdraw(float amount) {
        if (status.equals("active") && balance >= amount) {
            this.balance -= amount;
            this.lastActivityDate = new Date();
        }
    }

    public int getWalletId() { return walletId; }
    public float getBalance() { return balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

}
class SpotWallet extends Wallet {
    private String currency;
    private float maxBalanceLimit;

    public SpotWallet(int walletId, float balance, Date creationDate, String currency, float maxBalanceLimit) {
        super(walletId, balance, creationDate);
        this.currency = currency;
        this.maxBalanceLimit = maxBalanceLimit;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public float getMaxBalanceLimit() { return maxBalanceLimit; }
    public void setMaxBalanceLimit(float maxBalanceLimit) { this.maxBalanceLimit = maxBalanceLimit; }
}
class FiatWallet extends Wallet {
    private List<Owning> ownings;

    public FiatWallet(int walletId, float balance, Date creationDate, List<Owning> ownings) {
        super(walletId, balance, creationDate);
        this.ownings = ownings;
    }

    private int generateOwningId() {
        return ownings.size() + 1;
    }

    public List<Owning> getOwnings() {
        return ownings;
    }

    public void buyCoin(String coinCode, float amount, float exchangeRate) {
        if (balance >= amount) {
            // Deduct the amount from the wallet
            withdraw(amount);

            // Add a new Owning
            Owning newOwning = new Owning(ownings.size() + 1, amount / exchangeRate, coinCode);
            newOwning.setPurchaseDate(new Date());
            newOwning.setPurchaseRate(exchangeRate);
            ownings.add(newOwning);

            System.out.printf("Successfully bought %.4f %s at a rate of %.2f USDT per unit.\n",
                    amount / exchangeRate, coinCode, exchangeRate);
            System.out.printf("Remaining Fiat Wallet Balance: %.2f USDT\n", getBalance());
        } else {
            System.out.println("Insufficient balance to buy coins.");
        }
    }
    public void sellCoin(String coinCode, float usdtAmount, float exchangeRate) {
        Owning selectedOwning = null;

        // Check if the user owns the coin
        for (Owning owning : ownings) {
            if (owning.getCoin().equalsIgnoreCase(coinCode)) {
                selectedOwning = owning;
                break;
            }
        }

        if (selectedOwning == null) {
            System.out.println("You do not own any " + coinCode + " to sell.");
            return;
        }

        // Calculate how much of the coin is needed to sell the given USDT amount
        float coinNeeded = usdtAmount / exchangeRate;

        // Check if the user owns enough coin to sell
        if (selectedOwning.getAmount() < coinNeeded) {
            System.out.printf("Insufficient %s to sell. You own %.4f, but you need %.4f to sell %.2f USDT.\n",
                    coinCode, selectedOwning.getAmount(), coinNeeded, usdtAmount);
            return;
        }

        // Adjust the owning and add the USDT amount to the wallet
        selectedOwning.setAmount(selectedOwning.getAmount() - coinNeeded);
        deposit(usdtAmount);

        System.out.printf("Successfully sold %.4f %s for %.2f USDT at a rate of %.2f USDT per unit.\n",
                coinNeeded, coinCode, usdtAmount, exchangeRate);

        // Remove the owning if the amount reaches zero
        if (selectedOwning.getAmount() <= 0) {
            ownings.remove(selectedOwning);
            System.out.println("You no longer own any " + coinCode + ".");
        }
    }


    public void viewOwnings(APIController api) {
        if (ownings.isEmpty()) {
            System.out.println("No coins owned in the Fiat Wallet.");
        } else {
            System.out.println("Owned Coins:");
            for (Owning owning : ownings) {
                // Fetch the current exchange rate for the coin
                float currentRate = api.getExchangeRate(owning.getCoin(), "USDT");
                if (currentRate <= 0) {
                    System.out.printf("Coin: %s | Amount: %.4f | Purchase Rate: %.2f USDT | Purchase Date: %s | Current Value: Unable to fetch rate\n",
                            owning.getCoin(), owning.getAmount(), owning.getPurchaseRate(), owning.getPurchaseDate());
                } else {
                    // Calculate the current USDT value
                    float currentValue = owning.getAmount() * currentRate;
                    System.out.printf("Coin: %s | Amount: %.4f | Purchase Rate: %.2f USDT | Purchase Date: %s | Current Value: %.2f USDT (Rate: %.2f USDT)\n",
                            owning.getCoin(), owning.getAmount(), owning.getPurchaseRate(), owning.getPurchaseDate(), currentValue, currentRate);
                }
            }
        }
    }


}
class User {
    private int userId;
    private String name;
    private Date birthDate;
    private String address;
    private String phone;
    private String email;
    //private SpotWallet spotWallet;
    //private FiatWallet fiatWallet;
    //private BankDetails bankDetails;
    //private List<Transaction> transactions;
    private static int IDs=1;


    Date accountCreationDate;
    Date lastLoginDate;
    String accountStatus;

    public User()
    {

    }
    public User(int userId, String name, Date birthDate, String phone, String email,
                Date accountCreationDate, Date lastLoginDate, String accountStatus) {
        this.userId = userId;
        this.name = name;
        this.birthDate = birthDate; // Use the provided birthDate
        this.phone = phone;
        this.email = email;
        this.accountCreationDate = accountCreationDate; // Use the provided accountCreationDate
        this.lastLoginDate = lastLoginDate; // Use the provided lastLoginDate
        this.accountStatus = accountStatus; // Use the provided accountStatus
        IDs++; // Increment the static ID counter
    }

    public static int getIDs(){return IDs;}

    public int getUserId() { return userId; }

    public Date getAccountCreationDate() { return accountCreationDate; }
    public Date getLastLoginDate() { return lastLoginDate; }
    public String getAccountStatus() { return accountStatus; }
    public String getName() { return name; }
    public Date getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setUserId(int userId) {this.userId = userId;}


    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public void setAddress(String address) {
        this.address = address;

    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
class Customer extends User{
    private SpotWallet spotWallet;
    private FiatWallet fiatWallet;
    private BankDetails bankDetails;
    private List<Transaction> transactions;


    Customer(int userId,String name,Date birthDate,String phone,String email,Date accountCreationDate,Date lastLoginDate,String accountStatus,SpotWallet spotWallet,FiatWallet fiatWallet,BankDetails bankDetails){
        super(userId,name, birthDate,phone,email, accountCreationDate,lastLoginDate,accountStatus);
        this.spotWallet = spotWallet;
        this.fiatWallet = fiatWallet;
        this.bankDetails = bankDetails;
        this.transactions = new ArrayList<>();
    }

    public SpotWallet getSpotWallet() { return spotWallet; }
    public FiatWallet getFiatWallet() { return fiatWallet; }
    public List<Transaction> getTransactions() { return transactions; }

    public void setSpotWallet(SpotWallet spotWallet) {this.spotWallet = spotWallet;}

    public BankDetails getBankDetails() {
        return bankDetails;
    }
    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;

    }

    public void setFiatWallet(FiatWallet fiatWallet) {
        this.fiatWallet = fiatWallet;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void recordTransaction(float amount, String transactionType, String coin, float coinRate) {
        int transactionId = transactions.size() + 1; // Generate transaction ID
        Transaction transaction = new Transaction(transactionId, this, amount, new Date(), transactionType, coin, coinRate);
        transactions.add(transaction);

        transaction.saveTransactionToFile(transaction);

        System.out.println("Transaction recorded successfully.");
    }

    public void buyCoin(APIController api, String coinCode, float amount) {
        FiatWallet fiatWallet = getFiatWallet();

        // Fetch exchange rate for the coin
        float exchangeRate = api.getExchangeRate(coinCode, "USDT");
        if (exchangeRate <= 0) {
            System.out.println("Failed to retrieve the exchange rate for the coin. Purchase canceled.");
            return;
        }

        // Show purchase details
        float coinsToBuy = amount / exchangeRate;
        System.out.printf("Exchange Rate: 1 %s = %.2f USDT\n", coinCode, exchangeRate);
        System.out.printf("You will receive %.4f %s for %.2f USDT.\n", coinsToBuy, coinCode, amount);
        System.out.printf("Remaining Fiat Wallet Balance after purchase: %.2f USDT\n",
                fiatWallet.getBalance() - amount);

        // Ask for confirmation
        Scanner scanner = new Scanner(System.in);
        System.out.print("Confirm purchase? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            fiatWallet.buyCoin(coinCode, amount, exchangeRate);
            recordTransaction(amount, "buy", coinCode, exchangeRate); // Record the transaction
        } else {
            System.out.println("Purchase canceled.");
        }

    }
    public void sellCoin(APIController api, String coinCode, float usdtAmount) {
        FiatWallet fiatWallet = getFiatWallet();

        // Display all coin ownings before proceeding
        System.out.println("Your current coin holdings:");
        fiatWallet.viewOwnings(api);

        // Fetch exchange rate for the coin
        float exchangeRate = api.getExchangeRate(coinCode, "USDT");
        if (exchangeRate <= 0) {
            System.out.println("Failed to retrieve the exchange rate for the coin. Sale canceled.");
            return;
        }

        // Find the selected coin in the ownings
        Owning selectedOwning = null;
        for (Owning owning : fiatWallet.getOwnings()) {
            if (owning.getCoin().equalsIgnoreCase(coinCode)) {
                selectedOwning = owning;
                break;
            }
        }

        if (selectedOwning == null) {
            System.out.println("You do not own any " + coinCode + " to sell.");
            return;
        }

        // Calculate coin needed and remaining balance
        float coinNeeded = usdtAmount / exchangeRate;
        float remainingCoins = selectedOwning.getAmount() - coinNeeded;

        // Show details of the sale
        System.out.printf("\nExchange Rate: 1 %s = %.2f USDT\n", coinCode, exchangeRate);
        System.out.printf("To sell %.2f USDT, you need %.4f %s.\n", usdtAmount, coinNeeded, coinCode);
        if (remainingCoins < 0) {
            System.out.printf("Insufficient %s to sell. You own %.4f, but you need %.4f.\n",
                    coinCode, selectedOwning.getAmount(), coinNeeded);
            return;
        }
        System.out.printf("Remaining %s after sale: %.4f\n", coinCode, remainingCoins);
        System.out.printf("Remaining Fiat Wallet Balance after sale: %.2f USDT\n",
                fiatWallet.getBalance() + usdtAmount);

        // Ask for confirmation
        Scanner scanner = new Scanner(System.in);
        System.out.print("Confirm sale? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            fiatWallet.sellCoin(coinCode, usdtAmount, exchangeRate);
            recordTransaction(usdtAmount, "sell", coinCode, exchangeRate); // Record the transaction
        } else {
            System.out.println("Sale canceled.");
        }
    }

    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions available for this customer.");
        } else {
            System.out.println("Transactions for Customer: " + getName() + " (ID: " + getUserId() + ")");
            for (Transaction transaction : transactions) {
                System.out.printf("Transaction ID: %d | Type: %s | Coin: %s | Amount: %.2f | Rate: %.2f | Date: %s\n",
                        transaction.getTransactionId(), transaction.transactionType,
                        transaction.coin != null ? transaction.coin : "N/A",
                        transaction.amount, transaction.coinRate, transaction.timestamp);
            }
        }
    }

    public List<Transaction> applyFilters() {
        List<Transaction> suspiciousTransactions = new ArrayList<>();
        System.out.println("Applying filters to transaction history...");
        for (Transaction transaction1 : transactions)
        {
            if (transaction1.applyFilters_t(transaction1))
            {
                suspiciousTransactions.add(transaction1);
            }
        }
        return suspiciousTransactions;
    }

    public void flagForReview()
    {
        System.out.println("Flagging for review");
    }
}
class Admin extends User {
    // Constructor
    public Admin(int AdminId,String name,Date birthDate, String email, String phone,Date accountCreationDate,Date lastLoginDate, String accountStatus) {
        super(AdminId,name, birthDate,phone,email, accountCreationDate,lastLoginDate,accountStatus);
    }

    public void retrieveTransactionHistory() {
        System.out.println("Retrieving transaction history...");
        Transaction transaction = new Transaction(); // Assuming a default constructor in Transaction
        transaction.printTransactionHistory();
    }

    public void viewTransaction() {
        System.out.println("\n--- Viewing Transactions ---");
        List<Transaction> transactions = Transaction.fetchTransactionsFromFile();

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.printf("Transaction ID: %d | User ID: %d | User Name: %s | Type: %s | Coin: %s | Amount: %.2f | Rate: %.2f | Date: %s\n",
                        transaction.getTransactionId(),
                        transaction.getUser().getUserId(),
                        transaction.getUser().getName(),
                        transaction.transactionType,
                        transaction.coin != null ? transaction.coin : "N/A",
                        transaction.amount,
                        transaction.coinRate,
                        transaction.timestamp
                );
            }
        }
    }
    public void selectFeedback() {
        System.out.println("Selecting feedback...");
        // Logic for selecting feedback
    }
    public void flagForReview(Transaction transaction) {
        System.out.println("Flagging transaction for review...");
        transaction.flagForReview_t(transaction);
    }
    public void notifyTeam(Transaction transaction) {
        System.out.println("Notifying team about transaction ID: " + transaction.getTransactionId());
        // Logic for notifying team
    }
    public void reviewFeedback() {
        System.out.println("feedback...");
        // Logic for reviewing feedback
    }


}
class BankDetails {
    private int detailsId;
    private String cardNumber;
    private Date expiryDate;
    private String bankName;
    private String accountHolderName;
    private String billingAddress;

    public BankDetails(int detailsId, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress) {
        this.detailsId = detailsId;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.billingAddress = billingAddress;
    }

    public String getBankName() { return bankName; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setBillingAddress(String billingAddress) {this.billingAddress = billingAddress;}
    public void setDetailsId(int detailsId) { this.detailsId = detailsId;}
    public int getDetailsId() { return detailsId; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardNumber() { return cardNumber; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBillingAddress() {return billingAddress;}
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }


}
class CryptoSystem {
    private List<Customer> customers;
    private List<Admin> admin;
    private Customer loggedInCustomer; // Add logged-in customer
    private Admin loggedInAdmin;
    private APIController api;
    float systemBalance;
    float transactionFees;
    Date lastBackupDate;
    int activeUsers;
    String systemStatus; // "operational" or "maintenance"
    CryptoSystem(String apiS) {
        api = new APIController(apiS);
        customers = new ArrayList<>();
        loggedInCustomer = null; // Initially, no customer is logged in

        admin = new ArrayList<>();

    }
    void printTopNumber(int i) {
        api.printTopCoins(i);
    }
    void printSingleCoin(String i) {
        api.printSingleCoin(i);
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
        //if the transaction id is found in the db
        //call the following function
        //transaction.flagForReview_t(transaction);
        //notifyTeam(transaction);
    }
    void notifyTeam(Transaction transaction) {
        transaction.notifyTeam(transaction);
        System.out.println("Team has been Notified.");
    }
    void addNewCustomer(String name, Date birthDate, String phone, String email, String accountStatus,
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
        BankDetails bankDetails = new BankDetails(bankDetailsId, cardNumber, expiryDate, bankName, accountHolderName, billingAddress);

        // Create and add new customer
        Customer newCustomer = new Customer(userId, name, birthDate, phone, email, currentDate, currentDate, accountStatus, spotWallet, fiatWallet, bankDetails);
        customers.add(newCustomer);

        setLoggedInCustomer(newCustomer);

        System.out.println("Customer added successfully! User ID: " + userId);
    }
    public void addNewAdmin(String name, Date birthDate, String phone, String email, String accountStatus) {
        // Generate a unique Admin ID
        int adminId = User.getIDs();

        // Create necessary dates
        Date currentDate = new Date(); // Current date for account creation and last login

        // Create and add new Admin
        Admin newAdmin = new Admin(adminId, name, birthDate, email, phone, currentDate, currentDate, accountStatus);
        admin.add(newAdmin);
        setLoggedInAdmin(newAdmin);

        System.out.println("Admin added successfully! Admin ID: " + adminId);
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

            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine();

            System.out.print("Enter Account Status (active/inactive): ");
            String accountStatus = scanner.nextLine();

            // Call addNewAdmin with gathered inputs
            addNewAdmin(name, birthDate, phone, email, accountStatus);
        } catch (Exception e) {
            System.out.println("An error occurred while processing input: " + e.getMessage());
        }
    }
    public void setLoggedInAdmin(Admin loggedInAdmin) {
        this.loggedInAdmin = loggedInAdmin;
    }
    void takeCustomerInput() {
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

            System.out.print("Enter Account Status (active/inactive): ");
            String accountStatus = scanner.nextLine();

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
                fiatOwnings.add(new Owning(i + 1, amount, coin));
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

            // Call addNewCustomer with gathered inputs
            addNewCustomer(name, birthDate, phone, email, accountStatus, spotWalletBalance, currency, maxBalanceLimit,
                    cardNumber, expiryDate, bankName, accountHolderName, billingAddress, fiatWalletBalance, fiatOwnings);
        } catch (Exception e) {
            System.out.println("An error occurred while processing input: " + e.getMessage());
        }
    }
    void viewAllCustomers() {
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
    void setLoggedInCustomer(Customer customer) {
        loggedInCustomer = customer;
        if (customer != null) {
            System.out.println("Logged in as: " + customer.getName() + " (ID: " + customer.getUserId() + ")");
        } else {
            System.out.println("Logged out successfully.");
        }
    }
    Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }
    Admin getLoggedInAdmin()
    {
        return loggedInAdmin;
    }
    List<Customer> getCustomers(){
        return customers;
    }
    void depositToSpotWallet() {
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
            System.out.printf("Deposit successful! New Spot Wallet Balance: %.2f\n", spotWallet.getBalance());
        } else {
            System.out.println("Deposit canceled.");
        }
    }
    void withdrawFromSpotWallet() {
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
            System.out.printf("Withdrawal successful! New Spot Wallet Balance: %.2f\n", spotWallet.getBalance());
        } else {
            System.out.println("Withdrawal canceled.");
        }
    }
    void transferBetweenWallets() {
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
        float exchangeRate = api.getExchangeRate("USDT", "USD");
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

        System.out.printf("Current %s Wallet Balance: %.2f\n", (choice == 1) ? "Spot" : "Fiat", sourceWallet.getBalance());
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

        System.out.printf("New %s Wallet Balance (after transfer): %.2f\n", (choice == 1) ? "Spot" : "Fiat", sourceWallet.getBalance() - transferAmount);
        System.out.printf("New %s Wallet Balance (after transfer): %.2f\n", (choice == 1) ? "Fiat" : "Spot", targetWallet.getBalance() + convertedAmount);

        System.out.print("Confirm transfer? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            sourceWallet.withdraw(transferAmount);
            targetWallet.deposit(convertedAmount);
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

        loggedInCustomer.buyCoin(api, coinCode, amount);
    }
    public void sellCoinForLoggedInCustomer() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }
        loggedInCustomer.getFiatWallet().viewOwnings(api);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the code of the coin you want to sell: ");
        String coinCode = scanner.nextLine();

        System.out.print("Enter the amount in USDT you want to receive: ");
        float usdtAmount = scanner.nextFloat();

        loggedInCustomer.sellCoin(api, coinCode, usdtAmount);
    }
    public void viewCustomerOwnings() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }
        loggedInCustomer.getFiatWallet().viewOwnings(api);
    }
    public void viewFeedbacks() {
        System.out.println("Displaying all feedbacks...");

    }
    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message);

    }
    public boolean register(User user) {
        if (user == null) {
            System.out.println("No user is logged in. Please log in to register a wallet.");
            return false;
        }

        System.out.println("Registering a new wallet for user: " + user.getName());
        AuthService authService = new AuthService();
        if (authService.registerNewUser(this)==true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean giveFeedback(int customerid){
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to register a wallet.");
            return false;
        }
        //Feedback feedback(customerid);
        //feedback.
        return true;

    }
    public void referFriend() {
        Referral refer=new Referral();
        refer.referFriend();
    }
    public void viewPortfolio(int UserID)
    {
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
    public void transferFIAT() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform a transfer.");

        }

        TransferService transferService = new TransferService();
        System.out.println("\n--- FIAT Transfer Form ---");
        transferService.transferFIAT(this);
    }

}
class WalletIDGenerator {
    private static int walletIdCounter = 1;

    static int generateWalletId() {
        return walletIdCounter++;
    }
}
class BankDetailsIDGenerator {
    private static int detailsIdCounter = 1;

    static int generateDetailsId() {
        return detailsIdCounter++;
    }
}
//
//public class Main {
//    public static void main(String[] args) {
//        CryptoSystem csMain = new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
//        Scanner sc = new Scanner(System.in);
//
//        while (true) {
//            Customer loggedInCustomer = csMain.getLoggedInCustomer();
//            System.out.println("\n--- Crypto System Menu ---");
//            if (loggedInCustomer != null) {
//                System.out.println("Logged in as: " + loggedInCustomer.getName() + " (ID: " + loggedInCustomer.getUserId() + ")");
//            } else {
//                System.out.println("No customer is logged in.");
//            }
//
//            System.out.println("1. Display top coins");
//            System.out.println("2. Display details of a single coin");
//            System.out.println("3. Add a new customer");
//            System.out.println("4. View all customer details");
//            System.out.println("5. Change logged-in customer");
//            System.out.println("6. Deposit into Spot Wallet");
//            System.out.println("7. Withdraw from Spot Wallet");
//            System.out.println("8. Transfer between Spot and Fiat Wallet");
//            System.out.println("9. Buy Coin");
//            System.out.println("10. Sell Coin");
//            System.out.println("11. View Owned Coins");
//            System.out.println("12. View Transactions");
//            System.out.println("13. Exit");
//            System.out.print("Choose an option: ");
//
//            int choice = sc.nextInt();
//            sc.nextLine(); // Consume leftover newline
//
//            switch (choice) {
//                case 1:
//                    System.out.print("Enter the number of top coins to display: ");
//                    int numCoins = sc.nextInt();
//                    sc.nextLine();
//                    csMain.printTopNumber(numCoins);
//                    break;
//
//                case 2:
//                    System.out.print("Enter the code of the coin: ");
//                    String coinCode = sc.nextLine();
//                    csMain.printSingleCoin(coinCode);
//                    break;
//
//                case 3:
//                    System.out.println("Enter details to add a new customer:");
//                    csMain.takeCustomerInput();
//                    break;
//
//                case 4:
//                    System.out.println("Customer Details:");
//                    csMain.viewAllCustomers();
//                    break;
//
//                case 5:
//                    System.out.println("Change logged-in customer:");
//                    System.out.println("Available Customers:");
//                    csMain.viewAllCustomers();
//                    System.out.print("Enter the ID of the customer to log in (or 0 to log out): ");
//                    int customerId = sc.nextInt();
//                    sc.nextLine();
//                    if (customerId == 0) {
//                        csMain.setLoggedInCustomer(null);
//                    } else {
//                        Customer customerToLogIn = null;
//                        for (Customer customer : csMain.getCustomers()) {
//                            if (customer.getUserId() == customerId) {
//                                customerToLogIn = customer;
//                                break;
//                            }
//                        }
//                        if (customerToLogIn != null) {
//                            csMain.setLoggedInCustomer(customerToLogIn);
//                        } else {
//                            System.out.println("Invalid customer ID.");
//                        }
//                    }
//                    break;
//
//                case 6:
//                    csMain.depositToSpotWallet();
//                    break;
//
//                case 7:
//                    csMain.withdrawFromSpotWallet();
//                    break;
//
//                case 8:
//                    csMain.transferBetweenWallets();
//                    break;
//
//                case 9:
//                    csMain.buyCoinForLoggedInCustomer();
//                    break;
//
//                case 10:
//                    csMain.sellCoinForLoggedInCustomer();
//                    break;
//
//                case 11:
//                    csMain.viewCustomerOwnings();
//                    break;
//
//                case 12:
//                    csMain.viewCustomerTransactions();
//
//                    break;
//
//                case 13:
//                    System.out.println("Exiting the system. Goodbye!");
//                    sc.close();
//                    return;
//
//                default:
//                    System.out.println("Invalid option. Please try again.");
//            }
//        }
//    }
//}




//public class Main {
//    public static void main(String[] args) {
//        CryptoSystem csMain = new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
//        Scanner sc = new Scanner(System.in);
//
//        Admin admin = new Admin(1, "Admin1", null, "admin@example.com", "1234567890", new Date(), new Date(), "active");
//
//        while (true) {
//            System.out.println("\n--- Crypto System Menu ---");
//            System.out.println("1. Record a Transaction");
//            System.out.println("2. View Transactions (Admin)");
//            System.out.println("3. Exit");
//            System.out.print("Choose an option: ");
//
//            int choice = sc.nextInt();
//            sc.nextLine(); // Consume newline
//
//            switch (choice) {
//                case 1:
//                    System.out.print("Enter User ID: ");
//                    int userId = sc.nextInt();
//                    sc.nextLine();
//
//                    System.out.print("Enter User Name: ");
//                    String userName = sc.nextLine();
//
//                    User user = new User(userId, userName, null, null, null, new Date(), new Date(), "active");
//
//                    System.out.print("Enter Transaction Type (buy/sell): ");
//                    String transactionType = sc.nextLine();
//
//                    System.out.print("Enter Coin Code (or N/A for fiat): ");
//                    String coin = sc.nextLine();
//
//                    System.out.print("Enter Amount: ");
//                    float amount = sc.nextFloat();
//
//                    System.out.print("Enter Coin Rate: ");
//                    float rate = sc.nextFloat();
//
//                    Transaction transaction = new Transaction(TransactionIDGenerator.generate(), user, amount, new Date(), transactionType, coin, rate);
//                    Transaction.saveTransactionToFile(transaction);
//                    System.out.println("Transaction recorded successfully.");
//                    break;
//
//                case 2:
//                    admin.viewTransaction();
//                    break;
//
//                case 3:
//                    System.out.println("Exiting the system. Goodbye!");
//                    sc.close();
//                    return;
//
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//}

public class Main {
    public static void main(String[] args) {
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
                    if (csMain.giveFeedback(loggedInCustomer.getUserId())==true)
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
                    if (csMain.register(loggedInCustomer)==true)
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
