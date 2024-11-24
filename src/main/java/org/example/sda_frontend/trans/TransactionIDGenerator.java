package org.example.sda_frontend.trans;

public class TransactionIDGenerator {
    private static int counter = 1;

    public static int generate() {
        return counter++;
    }
}