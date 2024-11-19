package org.example;

import java.util.Date;
import java.util.List;

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

