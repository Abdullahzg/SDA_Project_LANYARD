package org.example;

class WalletIDGenerator {
    private static int walletIdCounter = 1;

    static int generateWalletId() {
        return walletIdCounter++;
    }
}