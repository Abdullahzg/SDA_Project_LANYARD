package org.example.trans;

public class TransactionIDGenerator {
    private static int counter = 1;

    public static int generate() {
        return counter++;
    }
}