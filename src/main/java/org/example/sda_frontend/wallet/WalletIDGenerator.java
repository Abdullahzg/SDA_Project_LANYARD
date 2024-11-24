package org.example.sda_frontend.wallet;

public class WalletIDGenerator {
    private static int walletIdCounter = 1;

    public static int generateWalletId() {
        return walletIdCounter++;
    }
}
