package org.example.trans;
import org.example.controller.CryptoSystem;
import org.example.user.Customer;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;
import java.util.Date;

public class TransferService {
    public void transferFIAT(CryptoSystem cryptoSystem) {
        Scanner scanner = new Scanner(System.in);

        // Display the form to get transfer details
        System.out.print("Enter the Receiver's User ID: ");
        int receiverId = scanner.nextInt();

        System.out.print("Enter the amount to transfer: ");
        float amount = scanner.nextFloat();

        submitForm(cryptoSystem, receiverId, amount);
    }

    public void submitForm(CryptoSystem cryptoSystem, int receiverId, float amount) {
        System.out.println("\nTransfer Form");
        boolean isValid = validateForm(cryptoSystem, receiverId, amount);
        if (!isValid) {
            System.out.println("Validation failed. Transfer cannot proceed.");
            return;
        }

        float senderBalance = fetchBalance(cryptoSystem);
        if (senderBalance < amount) {
            System.out.println("Insufficient balance for the transfer.");
        }
    }

    private boolean validateForm(CryptoSystem cryptoSystem, int receiverId, float amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Amount must be greater than zero.");
            return false;
        }
        Customer sender = cryptoSystem.getLoggedInCustomer();
        float senderBalance = fetchBalance(cryptoSystem);
        if (senderBalance < amount) {
            System.out.println("Insufficient balance for the transfer.");
            return false;
        }


        // Check if receiver exists
        //add the sql query to check if the customer (receiver) exists or not
//        if (receiver == null) {
//            System.out.println("Receiver with the specified User ID does not exist.");
//            return false;
//        }

        updateBalance(sender.getUserId(), senderBalance - amount); // Deduct amount from sender
        //yahan par sql query add karni hai to find the customer in the db and store it in receiver
        //Customer receiver = cryptoSystem.getCustomerById(receiverId);
        float receiverBalance = 0 ;
        //receiverBalance=receiver.getFiatWallet().getBalance();
        updateBalance(receiverId, receiverBalance + amount); // Add amount to receiver
        //recordTransaction(sender, receiver, amount);
        System.out.println("Validation successful.");
        return true;
    }

    public void updateBalance(int customerId, float newBalance) {
        // write sql query for updating balance of customers in database
        System.out.printf("Updating balance for User ID %d to %.2f...\n", customerId, newBalance);

    }

    public void recordTransaction(Customer sender, Customer receiver, float amount) {
        Transaction transaction = new Transaction(
                TransactionIDGenerator.generate(),
                sender, // Sender details
                amount,
                new Date(),
                "transfer to friend",
                null,
                0.0f
        );
        Transaction.saveTransactionToFile(transaction);

        System.out.println("Transaction recorded successfully.");
    }

    private float fetchBalance(@NotNull CryptoSystem cryptoSystem) {
        Customer sender = cryptoSystem.getLoggedInCustomer();
        if (sender == null) {
            System.out.println("No sender is logged in.");
            return 0;
        }

        return sender.getFiatWallet().getBalance();
    }

}
