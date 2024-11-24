package org.example.sda_frontend.user;

import org.example.sda_frontend.ai.APIController;
import org.example.sda_frontend.bank.BankDetails;
import org.example.sda_frontend.currency.Owning;
import org.example.sda_frontend.trans.Transaction;
import org.example.sda_frontend.trans.TransactionIDGenerator;
import org.example.sda_frontend.useractions.Comments;
import org.example.sda_frontend.useractions.Feedback;
import org.example.sda_frontend.wallet.FiatWallet;
import org.example.sda_frontend.wallet.SpotWallet;
import org.json.JSONObject;


import org.example.sda_frontend.db.DBHandler;

import org.example.sda_frontend.db.models.user.CustomerModel;

import org.example.sda_frontend.db.models.user.UserModel;

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

    public static boolean addNewCustomerDB(User user, SpotWallet spotWallet, FiatWallet fiatWallet, BankDetails bankDetails) {
        return DBHandler.saveCustomer(new CustomerModel(new UserModel(user.getName(), user.getBirthDate(), user.getAddress(), user.getPhone(), user.getEmail(), new Date(), new Date(), "active"), spotWallet, fiatWallet, bankDetails));
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
        int transactionId = TransactionIDGenerator.generate(); // Generate transaction ID
        Transaction transaction = new Transaction(transactionId, this, amount, new Date(), transactionType, coin, coinRate);
        transactions.add(transaction);
        Transaction.allTransactions.add(transaction);

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
    public void buyCoinn(APIController api, String coinCode, float amount) {
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
    public void sellCoinn(APIController api, String coinCode, float usdtAmount) {
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

    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions available for this customer.");
        } else {
            System.out.println("Transactions for Customer: " + getName() + " (ID: " + getUserId() + ")");
            for (Transaction transaction : transactions) {
                System.out.printf("Transaction ID: %d | Type: %s | Coin: %s | Amount: %.2f | Rate: %.2f | Date: %s\n",
                        transaction.getTransactionId(), transaction.transactionType,
                        transaction.coin != null ? transaction.coin : "N/A",
                        transaction.getAmount(), transaction.getCoinRate(), transaction.timestamp);
            }
        }
    }
    public String getTransactionsAsString(APIController api) {
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

    public String getGlobalTransactionsAsString() {
        if (transactions.isEmpty()) {
            return ""; // Return an empty string if there are no transactions
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = Transaction.allTransactions.size() - 1; i >= 0; i--) {
                Transaction transaction = transactions.get(i);

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
    }
    public String getSingleTransactionsAsString(int id, APIController api) {
        if (transactions.isEmpty()) {
            return ""; // Return an empty string if there are no transactions
        } else {
            for (Transaction transaction: Transaction.allTransactions) {
                if(transaction.getTransactionId()==id){
                    StringBuilder sb = new StringBuilder();
                    JSONObject jj=api.giveSingleCoin(transaction.coin);
                    String image= jj.getString("png32");
                    sb.append(transaction.getUser().getName()).append(",")
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
    }
    public String getTransactionComments(int id, APIController api) {
        if (transactions.isEmpty()) {
            return ""; // Return an empty string if there are no transactions
        } else {
            for (Transaction transaction: Transaction.allTransactions) {
                if(transaction.getTransactionId()==id){
                    StringBuilder sb = new StringBuilder();
                    List<Comments> commentsList = new ArrayList<>(transaction.getCoomments());
                    Collections.reverse(commentsList);
                    for(Comments comment: commentsList) {
                        sb.append(comment.getUserID().getName()).append(",")
                                .append(comment.getComment()).append(",")
                                .append(transaction.timestamp.toString()).append("\n");
                    }

                    return sb.toString();
                }

            }

            return null;
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

    public boolean giveFeedback() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Subject: ");
        String subject = scanner.nextLine();

        System.out.print("Enter Feedback: ");
        String feedback = scanner.nextLine();

        System.out.print("Enter Priority Level (1-3): ");
        int priorityLevel = scanner.nextInt();

        while (priorityLevel < 1 || priorityLevel > 3) {
            System.out.println("Invalid Priority Level");
            System.out.print("Enter Priority Level (1-3): ");
            priorityLevel = scanner.nextInt();
        }
        scanner.nextLine();

        Feedback feedbackObj = new Feedback(Feedback.generateFeedbackId(), subject, getUserId(), feedback, priorityLevel);
        feedbacks.add(feedbackObj);
        return Feedback.addFeedbackToDB(feedbackObj, this);
    }

    public String getStatus() {
        return "Customer";
    }

    // In Customer.java
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
}
