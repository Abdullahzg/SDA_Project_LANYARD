package org.example.sda_frontend.controller;

import org.example.sda_frontend.ai.AIAdviser;
import org.example.sda_frontend.bank.BankDetails;
import org.example.sda_frontend.bank.BankDetailsIDGenerator;
import org.example.sda_frontend.trans.Transaction;
import org.example.sda_frontend.useractions.*;
import org.example.sda_frontend.user.Customer;
import org.example.sda_frontend.user.User;
import org.example.sda_frontend.wallet.FiatWallet;
import org.example.sda_frontend.wallet.SpotWallet;
import org.example.sda_frontend.wallet.Wallet;
import org.example.sda_frontend.wallet.WalletIDGenerator;
import org.example.sda_frontend.ai.APIController;
import org.example.sda_frontend.currency.Owning;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class CryptoSystem {
    private static CryptoSystem instance;
    private List<Customer> customers;
    private static List<Transaction> globalTransactions;
    private Customer loggedInCustomer; // Add logged-in customer
    private APIController api;
    private final AIAdviser ai = new AIAdviser();


    private CryptoSystem(String apiS) {
        api = new APIController(apiS);
        customers = new ArrayList<>();
        loggedInCustomer = null;
        globalTransactions = null;
    }

    public static CryptoSystem getInstance() {
        if (instance == null) {
            instance = new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
        }
        return instance;
    }

    public static void makeNewInstance(){
        instance=new CryptoSystem("81c671a9-bdd6-4752-b892-76cb9691060c");
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
        List<Comments> comments = transaction.getTransactionComments();
        Collections.reverse(comments);
        StringBuilder sb = new StringBuilder();
        for(Comments comment: comments) {
            sb.append(comment.getCustomer().getName()).append(",")
                    .append(comment.getComment()).append(",")
                    .append(transaction.timestamp.toString()).append("\n");
        }
        return sb.toString();

        //???? what is this below
        //loggedInCustomer.applyFilters();
    }

    public JSONObject giveSingleCoin(String i) {
        return api.giveSingleCoin(i);
    }
    public JSONArray giveTopCoins(int i){
        return api.giveTopCoins(i);
    }
    public void depositToSpotWalletn(float depositAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();

        spotWallet.deposit(depositAmount);
        spotWallet.depositOrWithdrawDB("Withdrawal");
    }
    public void withdrawFromSpotWalletn(float withdrawAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();
        spotWallet.withdraw(withdrawAmount);
        spotWallet.depositOrWithdrawDB("Withdrawal");
    }
    public void transferBetweenWalletsn(int choice, float transferAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        SpotWallet spotWallet = loggedInCustomer.getSpotWallet();
        FiatWallet fiatWallet = loggedInCustomer.getFiatWallet();

        // Fetch the exchange rate for USDT to USD
        float exchangeRate = api.getExchangeRate("USDT", "USD");
        if (exchangeRate <= 0) {
            System.out.println("Failed to retrieve the exchange rate. Transfer canceled.");
            return;
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

        // If transferring from Spot (USD) to Fiat (USDT), invert the exchange rate
        if (choice == 1) {
            exchangeRate = 1 / exchangeRate;
            spotWallet.depositOrWithdrawDB("Withdrawal");
            fiatWallet.depositOrWithdrawDB("Deposit");
        }
        else{
            fiatWallet.depositOrWithdrawDB("Withdrawal");
            spotWallet.depositOrWithdrawDB("Deposit");
        }
    }

    public String viewCustomerOwningsn() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return null;
        }


        return  loggedInCustomer.getFiatWallet().viewOwningsn(api);
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
        Customer newCustomer = new Customer(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate, accountStatus,
                spotWallet, fiatWallet, bankDetails);

        User user = new User(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate, "active");
        boolean success = Customer.addNewCustomerDB(user, spotWallet, fiatWallet, bankDetails);
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
                // loggedInCustomer.getFiatWallet().withdraw((float)amount);
                // c.getFiatWallet().deposit((float)amount);

                FiatWallet senderWallet = loggedInCustomer.getFiatWallet();
                FiatWallet recipientWallet = c.getFiatWallet();
                senderWallet.transferFiatToAnotherUser(amount, recipientWallet);

                String emailSubject = "Lanyard | FIAT Transfer Notification";
                String emailBody = "You have received a FIAT transfer of $" + amount + " from " + loggedInCustomer.getName()
                        + " (" + loggedInCustomer.getEmail() + ").";

                Email email1 = new Email(c.getEmail(), emailSubject, emailBody);
                email1.sendEmail(false);

                return " ";
            }
        }

        return "User not found";
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
    public void buyCoinForLoggedInCustomern(String coinCode, float amount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.buyCoinn(api, coinCode, amount);
    }

    public float getCoinAmount(String code){
        float v= loggedInCustomer.getFiatWallet().getCoinAmount(code,api);
        float vv= api.getExchangeRate("USDT", code);
        return v/vv;
    }

    public int totalTransactions(){
        return globalTransactions.size();
    }
    public void sellCoinForLoggedInCustomern(String coinCode, float usdtAmount) {
        if (loggedInCustomer == null) {
            System.out.println("No customer is logged in. Please log in to perform this action.");
            return;
        }

        loggedInCustomer.sellCoinn(api, coinCode, usdtAmount);
    }

    public void addCommentToTransaction(Customer loggedInCustomer, int transactionID, String commentText){
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
            Comments newComment = new Comments(this.loggedInCustomer, commentText, transactionID);
            targetTransaction.addComment(loggedInCustomer, newComment);

            Email email = new Email(targetTransaction.getCustomer().getEmail(), "New Comment on Transaction",
                    "A new comment has been added to your transaction of $" + targetTransaction.getAmount() + " on " + targetTransaction.coin + ".");
            email.sendEmail(false);

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
