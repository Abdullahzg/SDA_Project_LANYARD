package org.example.sda_frontend.wallet;

import org.example.sda_frontend.ai.APIController;
import org.example.sda_frontend.currency.Owning;

import org.example.sda_frontend.db.DBHandler;

import java.util.Date;
import java.util.List;

public class FiatWallet extends Wallet {
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

            // Check if an Owning with the same coinCode exists
            Owning existingOwning = null;
            for (Owning owning : ownings) {
                if (owning.getCoin().equalsIgnoreCase(coinCode)) {
                    existingOwning = owning;
                    break;
                }
            }

            if (existingOwning != null) {
                // Update the existing Owning
                float newAmount = existingOwning.getAmount() + (amount / exchangeRate);
                existingOwning.setAmount(newAmount);

                // Optionally update the purchase rate and date
                existingOwning.setPurchaseDate(new Date());
                existingOwning.setPurchaseRate(exchangeRate);

                System.out.printf("Successfully added %.4f %s to your existing holding at a rate of %.2f USDT per unit.\n",
                        amount / exchangeRate, coinCode, exchangeRate);

                if (DBHandler.saveOrUpdateOwning(ownings.size() + 1, existingOwning, coinCode, amount, exchangeRate, this.walletId, this)) {
                    System.out.printf("Remaining Fiat Wallet Balance: %.2f USDT\n", getBalance());
                } else {
                    System.out.println("Failed to save the owning to the database.");
                }
            } else {
                // Create a new Owning
                Owning newOwning = new Owning(ownings.size() + 1, amount / exchangeRate, coinCode);
                newOwning.setPurchaseDate(new Date());
                newOwning.setPurchaseRate(exchangeRate);
                ownings.add(newOwning);

                System.out.printf("Successfully bought %.4f %s at a rate of %.2f USDT per unit.\n",
                        amount / exchangeRate, coinCode, exchangeRate);

                if (DBHandler.saveOrUpdateOwning(ownings.size() + 1, newOwning, coinCode, amount, exchangeRate, this.walletId, this)) {
                    System.out.printf("Remaining Fiat Wallet Balance: %.2f USDT\n", getBalance());
                } else {
                    System.out.println("Failed to save the owning to the database.");
                }
            }
        } else {
            System.out.println("Insufficient balance to buy coins.");
        }
    }

    public String viewOwnings(APIController api) {
        if (ownings.isEmpty()) {
            return "No coins owned in the Fiat Wallet.";
        }

        StringBuilder result = new StringBuilder();
        for (Owning owning : ownings) {
            // Fetch the current exchange rate for the coin
            float currentRate = api.getExchangeRate(owning.getCoin(), "USDT");
            if (currentRate <= 0) {
                result.append(String.format("%s,%.4f,Unable to fetch rate\n",
                        owning.getCoin(), owning.getAmount()));
            } else {
                // Calculate the current USDT value
                float currentValue = owning.getAmount() * currentRate;
                String images=api.giveSingleCoin(owning.getCoin()).getString("png32");
                result.append(String.format("%s,%s,%.4f,%.2f\n",
                        images,owning.getCoin(), owning.getAmount(), currentValue));
            }
        }

        return result.toString().trim(); // Remove trailing newline
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

        DBHandler.updateOwningAfterSell(selectedOwning, coinCode, usdtAmount, exchangeRate, this.walletId, this);
    }

    public float getCoinAmount(String code, APIController api){
        if (ownings.isEmpty()) {
            System.out.println("No coins owned in the Fiat Wallet.");
            return 0;
        } else {
            float amount=0;
            for (Owning owning : ownings) {
                // Fetch the current exchange rate for the coin
                if (owning.getCoin().equals(code)){
                    amount+=owning.getAmount();
                    break;
                }
            }
            return amount;
        }
    }

    @Override
    public void depositOrWithdrawDB(String type) {
        // Update the database
        DBHandler.depositOrWithdrawFiatWalletDB(this, type, false, 0.0);
    }

    public void transferFiatToAnotherUser(double amountToSend, FiatWallet recipientWallet) {
        recipientWallet.deposit(amountToSend);

        withdraw(amountToSend);

        // Update the database
        DBHandler.depositOrWithdrawFiatWalletDB(this, "Withdrawal", false, 0.0);
        DBHandler.depositOrWithdrawFiatWalletDB(recipientWallet, "Deposit", true, amountToSend);
    }

}
