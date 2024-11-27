package org.example.sda_frontend.controller;

import org.example.sda_frontend.ai.*;
import org.example.sda_frontend.trans.Transaction;
import org.example.sda_frontend.useractions.Email;
import org.example.sda_frontend.user.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class CryptoSystem {
    private static CryptoSystem instance = null;
    private List<Customer> customers = new ArrayList<>();
    private static List<Transaction> globalTransactions;
    private Customer loggedInCustomer; // Add logged-in customer
    private APIController api;
    private final AIAdviser ai = new AIAdviser();


    private CryptoSystem(String apiS) {
        api = new APIController(apiS);
        customers = new ArrayList<>();
        loggedInCustomer = null;
        globalTransactions = new ArrayList<>();
    }

    public float getFiatBalance(){
        return getLoggedInCustomer().getFiatWallet().getBalance();
    }
    public float getSpotBalance(){
        return getLoggedInCustomer().getSpotWallet().getBalance();
    }

    public static CryptoSystem getInstance() {
        if (instance == null) {
            instance = new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
        }
        return instance;
    }

    public static List<Transaction> getGlobalTransactions(){
        return globalTransactions;
    }
    public String getTransactionsAsString() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }
        return loggedInCustomer.getTransactionsAsString(api);
        //???? what is this below
        //loggedInCustomer.applyFilters();
    }
    public String getGlobalTransactionsAsString() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }
        globalTransactions = Transaction.getGlobalTransactionsAsString();

        assert globalTransactions != null;
        if (globalTransactions.isEmpty()) {
            return null; // Return an empty string if there are no transactions
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = globalTransactions.size() - 1; i >= 0; i--) {
                Transaction transaction = globalTransactions.get(i);
                sb.append(transaction.transactionType).append(",")
                        .append(transaction.coin != null ? transaction.coin : "N/A").append(",")
                        .append(String.format("%.2f", transaction.getAmount())).append(",")
                        .append(String.format("%.2f", transaction.getCoinRate())).append(",")
                        .append(transaction.timestamp.toString()).append(",")
                        .append(transaction.getTransactionId()).append("\n");
            }
            // Remove the last newline character if necessary
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        }

        //???? what is this below
        //loggedInCustomer.applyFilters();
    }
    public String getSingleTransactionsAsString(int transactionID) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }

        if (globalTransactions.isEmpty()) {
            return ""; // Return an empty string if there are no transactions
        } else {
            for (Transaction transaction: globalTransactions) {
                if(transaction.getTransactionId() == transactionID) {
                    StringBuilder sb = new StringBuilder();
                    JSONObject jj=api.giveSingleCoin(transaction.coin);
                    String image= jj.getString("png32");
                    sb.append(transaction.getCustomer().getName()).append(",")
                            .append(jj.getString("name")).append(",")
                            .append(image).append(",")
                            .append(transaction.transactionType).append(",")
                            .append(transaction.coin != null ? transaction.coin : "N/A").append(",")
                            .append(String.format("%.2f", transaction.getAmount())).append(",")
                            .append(String.format("%.2f", transaction.getCoinRate())).append(",")
                            .append(transaction.timestamp.toString()).append("\n");
                    return sb.toString();
                }
            }
            return null;
        }
        //???? what is this below
        //loggedInCustomer.applyFilters();
    }
    public String getTransactionComments(int i) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }

        // loggedInCustomer.getTransactionComments(i, api);
        Transaction transaction = getTransactionById(i);
        return transaction.getTransactionComments();
    }

    public JSONObject giveSingleCoin(String i) {
        return api.giveSingleCoin(i);
    }
    public JSONArray giveTopCoins(int i){
        return api.giveTopCoins(i);
    }
    public void depositToSpotWallet(float depositAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.getSpotWallet().deposit(depositAmount);
        loggedInCustomer.getSpotWallet().depositOrWithdrawDB("Deposit");
    }
    public void withdrawFromSpotWallet(float withdrawAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.getSpotWallet().withdraw(withdrawAmount);
        loggedInCustomer.getSpotWallet().depositOrWithdrawDB("Withdrawal");
    }
    public void transferBetweenWallets(int choice, float transferAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.transferBetweenWallets(choice, transferAmount, api);

    }

    public String viewCustomerOwnings() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }


        return  loggedInCustomer.getFiatWallet().viewOwnings(api);
    }

    public double getTotalCoinValuation(List<String> s){
        double a=0.0;
        for(String transaction: s){
            String[] details = transaction.split(",");
            a+= Float.parseFloat(details[3]);
        }
        return a;
    }

    public void addNewCustomer(String name, Date birthDate, String phone, String email, String accountStatus,
                               float spotWalletBalance, String currency, float maxBalanceLimit,
                               String cardNumber, Date expiryDate, String bankName, String accountHolderName,
                               String billingAddress, float fiatWalletBalance) {

        // Generate unique IDs
        int userId = User.getIDs();

        // Create necessary objects
        Date currentDate = new Date(); // Current date for creation

        // Create and add new customer
        Customer newCustomer = new Customer(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate, accountStatus,
                spotWalletBalance, currentDate, currency, maxBalanceLimit,
                fiatWalletBalance, currentDate,
                cardNumber, expiryDate, bankName, accountHolderName, billingAddress);

        User user = new User(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate, "active");
        boolean success = Customer.addNewCustomerDB(user, newCustomer.getSpotWallet(), newCustomer.getFiatWallet(),
                newCustomer.getBankDetails().getDetailsId(), cardNumber, expiryDate, bankName, accountHolderName,
                billingAddress);
        if(success) {
            customers.add(newCustomer);
            newCustomer = Customer.getCustomerByEmail(email);
            setLoggedInCustomer(newCustomer);
        }
    }

    public Customer getCustomerByEmail(String email) {
        return Customer.getCustomerByEmail(email);
    }

    public String getAIAdvice(String prompt){
        return ai.getAdvice(getLoggedInCustomer(), api, prompt);
    }

    public String performTransfer(String email, double amount){
        if(loggedInCustomer.getFiatWallet().getBalance()<amount){
            return "Insufficient Funds";
        }

        // load all customers into the system from database
        customers = Customer.getAllCustomersDB();

        for (Customer c: customers){
            if(c.getEmail().equals(email)){
                loggedInCustomer.performTransfer(c, amount);

                notifyUser("Lanyard | FIAT Transfer Notification",
                        "You have received a FIAT transfer of $" + amount + " from " + loggedInCustomer.getName()
                                + " (" + loggedInCustomer.getEmail() + ").",
                        c.getEmail());

                return " ";
            }
        }
        return "User not found";
    }

    public void notifyUser(String emailSubject, String emailBody, String recipientEmail){
        Email email = new Email(recipientEmail, emailSubject, emailBody);
        email.sendEmail(false);
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
    public void buyCoinForLoggedInCustomer(String coinCode, float amount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.buyCoin(api, coinCode, amount);
    }

    public float getCoinAmount(String code){
        float v= loggedInCustomer.getFiatWallet().getCoinAmount(code,api);
        float vv= api.getExchangeRate("USDT", code);
        return v/vv;
    }

    public int totalTransactions(){
        return globalTransactions.size();
    }
    public void sellCoinForLoggedInCustomer(String coinCode, float usdtAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.sellCoin(api, coinCode, usdtAmount);
    }

    public void addCommentToTransaction(int transactionID, String commentText){
        if (this.loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        // Assuming Transaction.allTransactions is accessible
        Transaction targetTransaction = null;
        for (Transaction t : globalTransactions){
            if (t.getTransactionId() == transactionID){
                targetTransaction = t;
                break;
            }
        }

        if (targetTransaction != null){
            loggedInCustomer.addCommentToTransaction(transactionID, commentText, targetTransaction);

            notifyUser("New Comment on Transaction",
                    "A new comment has been added to your transaction of $" + targetTransaction.getAmount() + " on " + targetTransaction.coin + ".",
                    targetTransaction.getCustomer().getEmail());

            System.out.println("Comment added to transaction ID " + transactionID);
        } else {
            System.out.println("Transaction with ID " + transactionID + " not found.");
        }
    }

    public Transaction getTransactionById(int transactionId) {
        for (Transaction transaction : globalTransactions) {
            if (transaction.getTransactionId() == transactionId) {
                return transaction;
            }
        }
        return null;
    }
}
