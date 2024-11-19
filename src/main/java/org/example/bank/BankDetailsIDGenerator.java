package org.example;

// Utility for generating unique Bank Details IDs
class BankDetailsIDGenerator {
    private static int detailsIdCounter = 1;

    static int generateDetailsId() {
        return detailsIdCounter++;
    }
}

