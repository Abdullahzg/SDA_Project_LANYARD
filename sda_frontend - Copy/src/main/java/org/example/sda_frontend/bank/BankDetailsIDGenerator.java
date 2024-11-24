package org.example.sda_frontend.bank;

// Utility for generating unique Bank Details IDs
public class BankDetailsIDGenerator {
    private static int detailsIdCounter = 1;

    public static int generateDetailsId() {
        return detailsIdCounter++;
    }
}

