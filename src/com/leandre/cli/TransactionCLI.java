package com.leandre.cli;

import com.leandre.account.Account;
import com.leandre.account.AccountManager;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.InvalidAccountException;
import com.leandre.exception.OverdraftExceededException;
import com.leandre.transaction.Transaction;
import com.leandre.transaction.TransactionManager;
import com.leandre.validation.InputValidator;

import java.util.List;
import java.util.Scanner;

public class TransactionCLI {

    public static void processTransaction(Scanner scanner, AccountManager accountManager, TransactionManager transactionManager) {
        System.out.println("\nPROCESS TRANSACTION");
        System.out.println("─".repeat(50));

        // Step 1: find the account
        // OLD: String accountNumber = InputValidator.readString(scanner, "Enter Account Number: ").toUpperCase();
        // NEW: regex-validated account number (must match ACC-XXX format)
        String accountNumber = InputValidator.readAccountNumber(scanner, "Enter Account Number: ");

        Account account;
        try {
            account = accountManager.findAccount(accountNumber);
        } catch (InvalidAccountException e) {
            System.out.println("\n✗ " + e.getMessage());
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
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
        System.out.println("3. Transfer to Account");
        int typeChoice = InputValidator.readInt(scanner, "Select type (1-3): ", 1, 3);

        if (typeChoice == 3) {
            handleTransfer(scanner, account, accountNumber, accountManager, transactionManager);
            return;
        }

        String type = typeChoice == 1 ? "DEPOSIT" : "WITHDRAWAL";

        // Step 4: enter amount
        double amount = InputValidator.readDouble(scanner, "Enter amount: $");

        double previousBalance = account.getBalance();
        double newBalance = account.previewBalance(amount, type);

        // Step 5: show confirmation
        String transactionId = Transaction.peekNextId();
        System.out.println("\nTRANSACTION CONFIRMATION");
        System.out.println("─".repeat(50));
        System.out.println("Transaction ID  : " + transactionId);
        System.out.println("Account         : " + accountNumber);
        System.out.println("Type            : " + type);
        System.out.printf("Amount          : $%,.2f%n", amount);
        System.out.printf("Previous Balance: $%,.2f%n", previousBalance);
        System.out.printf("New Balance     : $%,.2f%n", newBalance);
        System.out.println("─".repeat(50));

        // Step 6: confirm
        String confirm = InputValidator.readConfirmation(scanner, "Confirm transaction? (Y/N): ");

        if (confirm.equals("Y")) {
            try {
                transactionManager.executeTransaction(account, amount, type);
                System.out.println("\n✓ Transaction completed successfully!");
            } catch (OverdraftExceededException e) {
                System.out.printf("\n✗ Transaction failed: %s%n", e.getMessage());
                System.out.printf("  Current balance: $%,.2f | Attempted withdrawal: $%,.2f | Over limit by: $%,.2f%n",
                        e.getCurrentBalance(), e.getWithdrawAmount(), e.getAmountOverLimit());
            } catch (InsufficientFundsException e) {
                System.out.printf("\n✗ Transaction failed: %s%n", e.getMessage());
                System.out.printf("  Current balance: $%,.2f | Attempted withdrawal: $%,.2f | Deficit: $%,.2f%n",
                        e.getCurrentBalance(), e.getWithdrawAmount(), e.getDeficit());
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

        // OLD: String accountNumber = InputValidator.readString(scanner, "Enter Account Number: ").toUpperCase();
        // NEW: regex-validated account number (must match ACC-XXX format)
        String accountNumber = InputValidator.readAccountNumber(scanner, "Enter Account Number: ");

        // find the account
        Account account;
        try {
            account = accountManager.findAccount(accountNumber);
        } catch (InvalidAccountException e) {
            System.out.println("\n✗ " + e.getMessage());
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
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
            // Sort/filter menu
            System.out.println("\nSort & Filter options:");
            System.out.println("1. Default (chronological order)");
            System.out.println("2. Sort by Date (newest first)");
            System.out.println("3. Sort by Date (oldest first)");
            System.out.println("4. Sort by Amount (highest first)");
            System.out.println("5. Sort by Amount (lowest first)");
            System.out.println("6. Filter by Type (Deposits only)");
            System.out.println("7. Filter by Type (Withdrawals only)");
            int sortChoice = InputValidator.readInt(scanner, "Select option (1-7): ", 1, 7);

            // Get the sorted/filtered list based on user choice
            List<Transaction> displayList;
            String filterLabel = "All Transactions";

            switch (sortChoice) {
                case 2:
                    displayList = transactionManager.getByAccountSortedByDateDesc(accountNumber);
                    filterLabel = "Sorted by Date (newest first)";
                    break;
                case 3:
                    displayList = transactionManager.getByAccountSortedByDateAsc(accountNumber);
                    filterLabel = "Sorted by Date (oldest first)";
                    break;
                case 4:
                    displayList = transactionManager.getByAccountSortedByAmountDesc(accountNumber);
                    filterLabel = "Sorted by Amount (highest first)";
                    break;
                case 5:
                    displayList = transactionManager.getByAccountSortedByAmountAsc(accountNumber);
                    filterLabel = "Sorted by Amount (lowest first)";
                    break;
                case 6:
                    displayList = transactionManager.getByAccountAndType(accountNumber, "DEPOSIT");
                    filterLabel = "Deposits only";
                    break;
                case 7:
                    displayList = transactionManager.getByAccountAndType(accountNumber, "WITHDRAWAL");
                    filterLabel = "Withdrawals only";
                    break;
                default:
                    displayList = transactionManager.getByAccount(accountNumber);
                    break;
            }

            // table header
            System.out.println("\nTRANSACTION HISTORY — " + filterLabel);
            System.out.println("─".repeat(70));
            System.out.printf("%-8s | %-18s | %-10s | %12s | %s%n",
                    "TXN ID", "DATE/TIME", "TYPE", "AMOUNT", "BALANCE");
            System.out.println("─".repeat(70));

            // table rows — now iterating the sorted/filtered list
            for (Transaction t : displayList) {
                String sign = (t.getType().equals("DEPOSIT") || t.getType().equals("TRANSFER_IN")) ? "+" : "-";
                System.out.printf("%-8s | %-18s | %-10s | %12s | $%,.2f%n",
                        t.getTransactionId(),
                        t.getFormattedTimestamp(),
                        t.getType(),
                        sign + String.format("$%,.2f", t.getAmount()),
                        t.getBalanceAfter());
            }
            System.out.println("─".repeat(70));

            // summary
            double deposits = transactionManager.calculateTotalDeposits(accountNumber);
            double withdrawals = transactionManager.calculateTotalWithdrawals(accountNumber);
            double netChange = deposits - withdrawals;

            System.out.println();
            System.out.println("Showing            : " + displayList.size() + " of " + count + " transactions");
            System.out.println("Total Transactions : " + count);
            System.out.printf("Total Deposits     : $%,.2f%n", deposits);
            System.out.printf("Total Withdrawals  : $%,.2f%n", withdrawals);
            System.out.printf("Net Change         : %s$%,.2f%n", netChange >= 0 ? "+" : "-", Math.abs(netChange));
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void handleTransfer(Scanner scanner, Account sourceAccount, String sourceNumber,
<<<<<<< HEAD
                                       AccountManager accountManager, TransactionManager transactionManager) {
=======
                                         AccountManager accountManager, TransactionManager transactionManager) {
>>>>>>> c5b385b (ft(validation): implement validation and include emails)
        String destNumber = InputValidator.readAccountNumber(scanner, "\nEnter Destination Account Number: ");

        if (sourceNumber.equals(destNumber)) {
            System.out.println("\n✗ Cannot transfer to the same account.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        Account destAccount;
        try {
            destAccount = accountManager.findAccount(destNumber);
        } catch (InvalidAccountException e) {
            System.out.println("\n✗ Destination account error: " + e.getMessage());
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nDestination Account:");
        System.out.println("Customer: " + destAccount.getCustomer().getName());
        System.out.println("Account Type: " + destAccount.getAccountType());

        double amount = InputValidator.readDouble(scanner, "\nEnter transfer amount: $");

        double sourcePrevBalance = sourceAccount.getBalance();
        double sourceNewBalance = sourceAccount.previewBalance(amount, "TRANSFER_OUT");

        System.out.println("\nTRANSFER CONFIRMATION");
        System.out.println("─".repeat(50));
        System.out.println("From Account    : " + sourceNumber + " (" + sourceAccount.getCustomer().getName() + ")");
        System.out.println("To Account      : " + destNumber + " (" + destAccount.getCustomer().getName() + ")");
        System.out.printf("Transfer Amount : $%,.2f%n", amount);
        System.out.println("─".repeat(50));
        System.out.printf("Your Balance    : $%,.2f → $%,.2f%n", sourcePrevBalance, sourceNewBalance);
        System.out.println("─".repeat(50));

        String confirm = InputValidator.readConfirmation(scanner, "Confirm transfer? (Y/N): ");

        if (confirm.equals("Y")) {
            try {
                transactionManager.executeTransfer(sourceAccount, destAccount, amount);
                System.out.println("\n✓ Transfer completed successfully!");
            } catch (OverdraftExceededException e) {
                System.out.printf("\n✗ Transfer failed: %s%n", e.getMessage());
                System.out.printf("  Current balance: $%,.2f | Attempted transfer: $%,.2f | Over limit by: $%,.2f%n",
                        e.getCurrentBalance(), e.getWithdrawAmount(), e.getAmountOverLimit());
            } catch (InsufficientFundsException e) {
                System.out.printf("\n✗ Transfer failed: %s%n", e.getMessage());
                System.out.printf("  Current balance: $%,.2f | Attempted transfer: $%,.2f | Deficit: $%,.2f%n",
                        e.getCurrentBalance(), e.getWithdrawAmount(), e.getDeficit());
            }
        } else {
            System.out.println("\n✗ Transfer cancelled.");
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}