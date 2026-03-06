package com.leandre.cli;

import com.leandre.account.Account;
import com.leandre.account.AccountManager;
import com.leandre.transaction.Transaction;
import com.leandre.transaction.TransactionManager;

import java.util.Scanner;

public class TransactionCLI {

    public static void processTransaction(Scanner scanner, AccountManager accountManager, TransactionManager transactionManager) {
        System.out.println("\nPROCESS TRANSACTION");
        System.out.println("─".repeat(50));

        // Step 1: find the account
        scanner.nextLine(); // clear buffer
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().toUpperCase();

        Account account = accountManager.findAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        // Step 2: display account details
        System.out.println("\nAccount Details:");
        System.out.println("Customer: " + account.getCustomer().getName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.printf("Current Balance: $%,.2f%n", account.getBalance());

        // Step 3: select transaction type
        System.out.println("\nTransaction type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        System.out.print("Select type (1-2): ");
        int typeChoice = scanner.nextInt();

        String type = typeChoice == 1 ? "DEPOSIT" : "WITHDRAWAL";

        // Step 4: enter amount
        System.out.print("Enter amount: $");
        double amount = scanner.nextDouble();

        // Step 5: calculate balances
        double previousBalance = account.getBalance();
        double newBalance = typeChoice == 1
                ? previousBalance + amount
                : previousBalance - amount;

        // Step 6: show confirmation
        String transactionId = "TXN" + String.format("%03d", Transaction.getTransactionCounter() + 1);
        System.out.println("\nTRANSACTION CONFIRMATION");
        System.out.println("─".repeat(50));
        System.out.println("Transaction ID  : " + transactionId);
        System.out.println("Account         : " + accountNumber);
        System.out.println("Type            : " + type);
        System.out.printf("Amount          : $%,.2f%n", amount);
        System.out.printf("Previous Balance: $%,.2f%n", previousBalance);
        System.out.printf("New Balance     : $%,.2f%n", newBalance);
        System.out.println("─".repeat(50));

        // Step 7: confirm
        scanner.nextLine(); // clear buffer
        System.out.print("Confirm transaction? (Y/N): ");
        String confirm = scanner.nextLine().toUpperCase();

        if (confirm.equals("Y")) {
            // update account balance
            account.setBalance(newBalance);

            // record the transaction
            Transaction transaction = new Transaction(accountNumber, type, amount, newBalance);
            transactionManager.addTransaction(transaction);

            System.out.println("\n✓ Transaction completed successfully!");
        } else {
            System.out.println("\n✗ Transaction cancelled.");
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void viewTransactionHistory(Scanner scanner, TransactionManager transactionManager) {
        System.out.println("\nTRANSACTION HISTORY");
        System.out.println("─".repeat(50));

        scanner.nextLine(); // clear buffer
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().toUpperCase();

        System.out.println(transactionManager.viewTransactionByAccount(accountNumber));

        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}