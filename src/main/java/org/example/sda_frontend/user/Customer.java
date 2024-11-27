package org.example.sda_frontend.user;

import org.example.sda_frontend.ai.APIController;
import org.example.sda_frontend.bank.BankDetails;
import org.example.sda_frontend.bank.BankDetailsIDGenerator;
import org.example.sda_frontend.controller.CryptoSystem;
import org.example.sda_frontend.currency.Owning;
import org.example.sda_frontend.trans.Transaction;
import org.example.sda_frontend.trans.TransactionIDGenerator;
import org.example.sda_frontend.useractions.Feedback;
import org.example.sda_frontend.wallet.FiatWallet;
import org.example.sda_frontend.wallet.SpotWallet;


import org.example.sda_frontend.db.DBHandler;

import org.example.sda_frontend.db.models.user.CustomerModel;

import org.example.sda_frontend.db.models.user.UserModel;
import org.example.sda_frontend.wallet.Wallet;
import org.example.sda_frontend.wallet.WalletIDGenerator;

import java.util.*;

public class Customer extends User{
    private SpotWallet spotWallet;
    private FiatWallet fiatWallet;
    private BankDetails bankDetails;
    private List<Transaction> transactions;
    private List<Feedback> feedbacks;


    public Customer(int userId, String name, Date birthDate, String address, String phone, String email, Date accountCreationDate, Date lastLoginDate, String accountStatus, SpotWallet spotWallet, FiatWallet fiatWallet, BankDetails bankDetails){
        super(userId,name, birthDate,address,phone,email, accountCreationDate,lastLoginDate,accountStatus);
        this.spotWallet = spotWallet;
        this.fiatWallet = fiatWallet;
        this.bankDetails = bankDetails;
        this.transactions = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }

    public Customer(int userId, String name, Date birthDate, String billingAddress, String phone, String email, Date currentDate, Date currentDate1, String accountStatus, SpotWallet spotWallet, FiatWallet fiatWallet, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress1) {
        super(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate1, accountStatus);
        this.spotWallet = spotWallet;
        this.fiatWallet = fiatWallet;
        int bankDetailsId = BankDetailsIDGenerator.generateDetailsId();
        this.bankDetails = new BankDetails(bankDetailsId, cardNumber, expiryDate, bankName, accountHolderName, billingAddress1);
        this.transactions = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }

    public Customer(int userId, String name, Date birthDate, String billingAddress, String phone, String email, Date currentDate, Date currentDate1, String accountStatus,
                        float spotWalletBalance, Date currentDate2, String currency, float maxBalanceLimit,
                        float fiatWalletBalance, Date currentDate3, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress1) {
        super(userId, name, birthDate, billingAddress, phone, email, currentDate, currentDate1, accountStatus);
        int spotWalletId = WalletIDGenerator.generateWalletId();
        int fiatWalletId = WalletIDGenerator.generateWalletId();
        this.spotWallet = new SpotWallet(spotWalletId, spotWalletBalance, currentDate2, currency, maxBalanceLimit);
        this.fiatWallet = new FiatWallet(fiatWalletId, fiatWalletBalance, currentDate3, new ArrayList<>());
        int bankDetailsId = BankDetailsIDGenerator.generateDetailsId();
        this.bankDetails = new BankDetails(bankDetailsId, cardNumber, expiryDate, bankName, accountHolderName, billingAddress1);
        this.transactions = new ArrayList<>();
    }

    public static boolean addNewCustomerDB(User user, SpotWallet spotWallet, FiatWallet fiatWallet, BankDetails bankDetails) {
        return DBHandler.saveCustomer(new CustomerModel(new UserModel(user.getName(), user.getBirthDate(), user.getAddress(), user.getPhone(), user.getEmail(), new Date(), new Date(), "active"),
                spotWallet, fiatWallet, bankDetails));
    }

    public static List<Customer> getAllCustomersDB() {
        List<CustomerModel> customerModels = DBHandler.getAllCustomersFromDB();
        List<Customer> customers = new ArrayList<>();
        for (CustomerModel customerModel : customerModels) {
            customers.add(new Customer(customerModel.getUser().getUserId(), customerModel.getUser().getName(), customerModel.getUser().getBirthDate(),
                    customerModel.getUser().getAddress(), customerModel.getUser().getPhone(), customerModel.getUser().getEmail(),
                    customerModel.getUser().getAccountCreationDate(), customerModel.getUser().getLastLoginDate(), customerModel.getUser().getAccountStatus(),
                    customerModel.getSpotWallet(),
                    customerModel.getFiatWallet(),
                    customerModel.getBankDetails()));
        }
        return customers;
    }

    public static boolean addNewCustomerDB(User user, SpotWallet spotWallet, FiatWallet fiatWallet, int bankDetailsId, String cardNumber, Date expiryDate, String bankName, String accountHolderName, String billingAddress) {
        return DBHandler.saveCustomer(new CustomerModel(new UserModel(user.getName(), user.getBirthDate(), user.getAddress(), user.getPhone(), user.getEmail(), new Date(), new Date(), "active"),
                spotWallet, fiatWallet, new BankDetails(bankDetailsId, cardNumber, expiryDate, bankName, accountHolderName, billingAddress)));
    }

    public SpotWallet getSpotWallet() { return spotWallet; }
    public void setSpotWallet(SpotWallet spotWallet) {this.spotWallet = spotWallet;}
    public FiatWallet getFiatWallet() { return fiatWallet; }
    public void setFiatWallet(FiatWallet fiatWallet) {
        this.fiatWallet = fiatWallet;
    }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public BankDetails getBankDetails() {
        return bankDetails;
    }
    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;

    }

    public void recordTransaction(float amount, String transactionType, String coin, float coinRate) {
        int transactionId = TransactionIDGenerator.generate(); // Generate transaction ID
        Transaction transaction = new Transaction(transactionId, this, amount, new Date(), transactionType, coin, coinRate);
        transactions.add(transaction);
        CryptoSystem.getGlobalTransactions().add(transaction);

        //transaction.saveTransactionToFile(transaction);
        Transaction.saveTransactionToDB(transaction, this, amount, transactionType, coin, coinRate);

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

        fiatWallet.buyCoin(coinCode, amount, exchangeRate);
        recordTransaction(amount, "Buy", coinCode, exchangeRate); // Record the transaction
    }
    public void sellCoin(APIController api, String coinCode, float usdtAmount) {
        FiatWallet fiatWallet = getFiatWallet();

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

        fiatWallet.sellCoin(coinCode, usdtAmount, exchangeRate);
        recordTransaction(usdtAmount, "Sell", coinCode, exchangeRate); // Record the transaction

    }

    public String getTransactionsAsString(APIController api) {
        // get all transactions from db
        transactions = Transaction.getCustomerSpecificTransactionsFromDB(getEmail());

        // print all transactions
        System.out.println("Transactions for Customer: " + getName() + " (ID: " + getUserId() + ")");
        for (Transaction transaction : transactions) {
            System.out.printf("Transaction ID: %d | Type: %s | Coin: %s | Amount: %.2f | Rate: %.2f | Date: %s\n",
                    transaction.getTransactionId(), transaction.transactionType,
                    transaction.coin != null ? transaction.coin : "N/A",
                    transaction.getAmount(), transaction.getCoinRate(), transaction.timestamp);
        }

        if (transactions.isEmpty()) {
            return ""; // Return an empty string if there are no transactions
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = transactions.size() - 1; i >= 0; i--) {
                Transaction transaction = transactions.get(i);
                String image= api.giveSingleCoin(transaction.coin).getString("png32");
                sb.append(image).append(",").append(transaction.transactionType).append(",")
                        .append(transaction.coin != null ? transaction.coin : "N/A").append(",")
                        .append(String.format("%.2f", transaction.getAmount())).append(",")
                        .append(String.format("%.2f", transaction.getCoinRate())).append(",")
                        .append(transaction.timestamp.toString()).append("\n");
            }
            // Remove the last newline character if necessary
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }

            return sb.toString();
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

    public String getStatus() {
        return "Customer";
    }

    public String getFinancialSummary(APIController api) {
        StringBuilder summary = new StringBuilder();
        summary.append("Fiat Wallet Balance: ").append(fiatWallet.getBalance()).append(" USDT\n");
        summary.append("Spot Wallet Balance: ").append(spotWallet.getBalance()).append(" USD\n");
        summary.append("Fiat Ownings:\n");
        for (Owning owning : fiatWallet.getOwnings()) {
            summary.append(owning.getCoin()).append(": ").append(owning.getAmount()).append(" (Rate: ")
                    .append(api.getExchangeRate(owning.getCoin(), "USDT")).append(" USDT)\n");
        }
        return summary.toString();
    }

    public void performTransfer(Customer c, double amount) {
        FiatWallet senderWallet = fiatWallet;
        FiatWallet recipientWallet = c.getFiatWallet();
        senderWallet.transferFiatToAnotherUser(amount, recipientWallet);
    }

    public void transferBetweenWallets(int choice, float transferAmount, APIController api) {
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

    public void addCommentToTransaction(int transactionID, String commentText, Transaction targetTransaction) {
        targetTransaction.addComment(this, commentText, transactionID);
    }
}
