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

        // Step 5: validate amount before proceeding
        if (amount <= 0) {
            System.out.println("\n✗ Amount must be positive.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            scanner.nextLine();
            return;
        }

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
            try {
                account.processTransaction(amount, type);

                // record the transaction
                Transaction transaction = new Transaction(accountNumber, type, amount, account.getBalance());
                transactionManager.addTransaction(transaction);

                System.out.println("\n✓ Transaction completed successfully!");
            } catch (IllegalArgumentException e) {
                System.out.println("\n✗ Transaction failed: " + e.getMessage());
            }
        } else {
            System.out.println("\n✗ Transaction cancelled.");
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }


    public static void viewTransactionHistory(Scanner scanner, AccountManager accountManager, TransactionManager transactionManager) {
        System.out.println("\nVIEW TRANSACTION HISTORY");
        System.out.println("─".repeat(30));

        scanner.nextLine(); // clear buffer
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine().toUpperCase();

        // find the account
        Account account = accountManager.findAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        // display account summary
        System.out.println("\nAccount: " + accountNumber + " - " + account.getCustomer().getName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.printf("Current Balance: $%,.2f%n", account.getBalance());

        System.out.println();
        System.out.println("─".repeat(70));

        int count = transactionManager.countByAccount(accountNumber);

        if (count == 0) {
            System.out.println("No transactions recorded for this account.");
            System.out.println("─".repeat(70));
        } else {
            // table header
            System.out.println("TRANSACTION HISTORY");
            System.out.println("─".repeat(70));
            System.out.printf("%-8s | %-18s | %-10s | %12s | %s%n",
                    "TXN ID", "DATE/TIME", "TYPE", "AMOUNT", "BALANCE");
            System.out.println("─".repeat(70));

            // table rows
            for (int i = 0; i < transactionManager.getTransactionCount(); i++) {
                Transaction t = transactionManager.getTransaction(i);
                if (t.getAccountNumber().equals(accountNumber)) {
                    String sign = t.getType().equals("DEPOSIT") ? "+" : "-";
                    System.out.printf("%-8s | %-18s | %-10s | %12s | $%,.2f%n",
                            t.getTransactionId(),
                            t.getTimestamp(),
                            t.getType(),
                            sign + String.format("$%,.2f", t.getAmount()),
                            t.getBalanceAfter());
                }
            }
            System.out.println("─".repeat(70));

            // summary
            double deposits = transactionManager.calculateTotalDeposits(accountNumber);
            double withdrawals = transactionManager.calculateTotalWithdrawals(accountNumber);
            double netChange = deposits - withdrawals;

            System.out.println();
            System.out.println("Total Transactions : " + count);
            System.out.printf("Total Deposits     : $%,.2f%n", deposits);
            System.out.printf("Total Withdrawals  : $%,.2f%n", withdrawals);
            System.out.printf("Net Change         : %s$%,.2f%n", netChange >= 0 ? "+" : "-", Math.abs(netChange));
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}