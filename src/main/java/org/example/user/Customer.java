package org.example.user;

import org.example.ai.APIController;
import org.example.bank.BankDetails;
import org.example.currency.Owning;
import org.example.transaction.Transaction;
import org.example.wallet.FiatWallet;
import org.example.wallet.SpotWallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Customer extends User{
    private SpotWallet spotWallet;
    private FiatWallet fiatWallet;
    private BankDetails bankDetails;
    private List<Transaction> transactions;


    public Customer(int userId, String name, Date birthDate, String phone, String email, Date accountCreationDate, Date lastLoginDate, String accountStatus, SpotWallet spotWallet, FiatWallet fiatWallet, BankDetails bankDetails){
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
                        transaction.getAmount(), transaction.getCoinRate(), transaction.timestamp);
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
