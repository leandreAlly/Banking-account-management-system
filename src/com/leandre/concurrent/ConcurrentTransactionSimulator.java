package com.leandre.concurrent;

import com.leandre.account.Account;
import com.leandre.account.AccountManager;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.InvalidAccountException;
import com.leandre.exception.OverdraftExceededException;
import com.leandre.transaction.TransactionManager;
import com.leandre.validation.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Demonstrates thread-safe concurrent transactions using:
 * 1. Thread + synchronized — multiple threads doing deposits/withdrawals simultaneously
 * 2. Parallel streams     — batch transaction simulation
 *
 * This class shows how synchronized methods on Account and TransactionManager
 * prevent race conditions and data inconsistencies.
 */
public class ConcurrentTransactionSimulator {

    // ── Option 1: Manual Thread simulation ───────────────────────────────

    public static void runThreadSimulation(Scanner scanner, AccountManager accountManager, TransactionManager transactionManager) {
        System.out.println("\nCONCURRENT TRANSACTION SIMULATION (Threads)");
        System.out.println("─".repeat(60));

        String accountNumber = InputValidator.readAccountNumber(scanner, "Enter Account Number to simulate on: ");

        Account account;
        try {
            account = accountManager.findAccount(accountNumber);
        } catch (InvalidAccountException e) {
            System.out.println("\n✗ " + e.getMessage());
            return;
        }

        System.out.printf("\nStarting balance: $%,.2f%n", account.getBalance());
        System.out.println("─".repeat(60));

        int numDeposits = InputValidator.readInt(scanner, "Number of concurrent deposits (1-20): ", 1, 20);
        double depositAmount = InputValidator.readDouble(scanner, "Amount per deposit: $");
        int numWithdrawals = InputValidator.readInt(scanner, "Number of concurrent withdrawals (1-20): ", 1, 20);
        double withdrawalAmount = InputValidator.readDouble(scanner, "Amount per withdrawal: $");

        System.out.println("\nLaunching " + numDeposits + " deposit threads and "
                + numWithdrawals + " withdrawal threads simultaneously...");
        System.out.println("─".repeat(60));

        double expectedBalance = account.getBalance()
                + (numDeposits * depositAmount)
                - (numWithdrawals * withdrawalAmount);

        // Create threads
        List<Thread> threads = new ArrayList<>();

        // Deposit threads
        for (int i = 0; i < numDeposits; i++) {
            final int threadNum = i + 1;
            Thread t = new Thread(() -> {
                try {
                    // synchronized is inside executeTransaction and Account.deposit
                    transactionManager.executeTransaction(account, depositAmount, "DEPOSIT");
                    System.out.printf("  [Deposit  Thread-%02d] +$%,.2f completed (balance: $%,.2f)%n",
                            threadNum, depositAmount, account.getBalance());
                } catch (InsufficientFundsException | OverdraftExceededException e) {
                    System.out.printf("  [Deposit  Thread-%02d] FAILED: %s%n", threadNum, e.getMessage());
                }
            }, "Deposit-" + threadNum);
            threads.add(t);
        }

        // Withdrawal threads
        for (int i = 0; i < numWithdrawals; i++) {
            final int threadNum = i + 1;
            Thread t = new Thread(() -> {
                try {
                    // synchronized prevents race condition: check-then-withdraw is atomic
                    transactionManager.executeTransaction(account, withdrawalAmount, "WITHDRAWAL");
                    System.out.printf("  [Withdraw Thread-%02d] -$%,.2f completed (balance: $%,.2f)%n",
                            threadNum, withdrawalAmount, account.getBalance());
                } catch (InsufficientFundsException | OverdraftExceededException e) {
                    System.out.printf("  [Withdraw Thread-%02d] FAILED: %s%n", threadNum, e.getMessage());
                }
            }, "Withdrawal-" + threadNum);
            threads.add(t);
        }

        // Start all threads at the same time
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Simulation interrupted: " + e.getMessage());
            }
        }

        // Results
        System.out.println("\n" + "─".repeat(60));
        System.out.println("SIMULATION RESULTS");
        System.out.println("─".repeat(60));
        System.out.printf("Final balance          : $%,.2f%n", account.getBalance());
        System.out.printf("Expected balance       : $%,.2f%n", expectedBalance);
        System.out.printf("Difference             : $%,.2f%n", Math.abs(account.getBalance() - expectedBalance));

        if (Math.abs(account.getBalance() - expectedBalance) < 0.01) {
            System.out.println("✓ Thread-safe! No race conditions detected.");
        } else {
            System.out.println("✗ Race condition detected! (this should not happen with synchronized)");
        }
        System.out.println("Total transactions     : " + transactionManager.countByAccount(accountNumber));
    }

    // ── Option 2: Parallel Stream batch simulation ───────────────────────

    public static void runParallelStreamSimulation(Scanner scanner, AccountManager accountManager, TransactionManager transactionManager) {
        System.out.println("\nBATCH TRANSACTION SIMULATION (Parallel Streams)");
        System.out.println("─".repeat(60));

        String accountNumber = InputValidator.readAccountNumber(scanner, "Enter Account Number to simulate on: ");

        Account account;
        try {
            account = accountManager.findAccount(accountNumber);
        } catch (InvalidAccountException e) {
            System.out.println("\n✗ " + e.getMessage());
            return;
        }

        System.out.printf("\nStarting balance: $%,.2f%n", account.getBalance());

        int batchSize = InputValidator.readInt(scanner, "Number of batch deposits (1-100): ", 1, 100);
        double amount = InputValidator.readDouble(scanner, "Amount per deposit: $");

        double expectedBalance = account.getBalance() + (batchSize * amount);

        System.out.println("\nProcessing " + batchSize + " deposits in parallel using parallel stream...");
        System.out.println("─".repeat(60));

        long startTime = System.currentTimeMillis();

        // Parallel stream processes deposits concurrently across multiple CPU cores
        // synchronized on Account.deposit() prevents race conditions
        List<String> results = IntStream.rangeClosed(1, batchSize)
                .parallel()
                .mapToObj(i -> {
                    try {
                        transactionManager.executeTransaction(account, amount, "DEPOSIT");
                        return String.format("  [Batch-%03d] +$%,.2f → balance: $%,.2f (%s)",
                                i, amount, account.getBalance(), Thread.currentThread().getName());
                    } catch (InsufficientFundsException | OverdraftExceededException e) {
                        return String.format("  [Batch-%03d] FAILED: %s", i, e.getMessage());
                    }
                })
                .collect(java.util.stream.Collectors.toList());

        long elapsed = System.currentTimeMillis() - startTime;

        // Print results
        results.forEach(System.out::println);

        // Summary
        System.out.println("\n" + "─".repeat(60));
        System.out.println("PARALLEL STREAM RESULTS");
        System.out.println("─".repeat(60));
        System.out.printf("Final balance          : $%,.2f%n", account.getBalance());
        System.out.printf("Expected balance       : $%,.2f%n", expectedBalance);
        System.out.printf("Difference             : $%,.2f%n", Math.abs(account.getBalance() - expectedBalance));
        System.out.println("Time elapsed           : " + elapsed + "ms");
        System.out.printf("Threads used           : parallel stream (ForkJoinPool)%n");

        if (Math.abs(account.getBalance() - expectedBalance) < 0.01) {
            System.out.println("✓ Thread-safe! Parallel stream produced consistent results.");
        } else {
            System.out.println("✗ Inconsistency detected!");
        }
    }
}
