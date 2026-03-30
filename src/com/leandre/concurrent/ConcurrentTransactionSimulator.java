package com.leandre.concurrent;

import com.leandre.account.Account;
import com.leandre.account.AccountManager;
import com.leandre.cli.ConsoleLogger;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.InvalidAccountException;
import com.leandre.exception.OverdraftExceededException;
import com.leandre.transaction.TransactionManager;
import com.leandre.validation.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Demonstrates thread-safe concurrent transactions using:
 * 1. Thread + synchronized — multiple threads doing deposits/withdrawals simultaneously
 * 2. Parallel streams     — batch transaction simulation
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
            ConsoleLogger.error(e.getMessage());
            return;
        }

        System.out.printf("\nStarting balance: $%,.2f%n", account.getBalance());
        System.out.println("─".repeat(60));

        int numDeposits = InputValidator.readInt(scanner, "Number of concurrent deposits (1-20): ", 1, 20);
        double depositAmount = InputValidator.readDouble(scanner, "Amount per deposit: $");
        int numWithdrawals = InputValidator.readInt(scanner, "Number of concurrent withdrawals (1-20): ", 1, 20);
        double withdrawalAmount = InputValidator.readDouble(scanner, "Amount per withdrawal: $");

        double expectedBalance = account.getBalance()
                + (numDeposits * depositAmount)
                - (numWithdrawals * withdrawalAmount);

        System.out.println();
        ConsoleLogger.system("Preparing " + (numDeposits + numWithdrawals) + " threads...");
        System.out.println("─".repeat(60));

        List<Thread> threads = new ArrayList<>();

        // Deposit threads
        for (int i = 0; i < numDeposits; i++) {
            final int threadNum = i + 1;
            String threadName = String.format("Deposit-%02d", threadNum);
            Thread t = new Thread(() -> {
                ConsoleLogger.threadStarted(threadName);
                try {
                    transactionManager.executeTransaction(account, depositAmount, "DEPOSIT");
                    ConsoleLogger.threadCompleted(threadName,
                            String.format("+$%,.2f → balance: $%,.2f", depositAmount, account.getBalance()));
                } catch (InsufficientFundsException | OverdraftExceededException e) {
                    ConsoleLogger.threadFailed(threadName, e.getMessage());
                }
            }, threadName);
            threads.add(t);
        }

        // Withdrawal threads
        for (int i = 0; i < numWithdrawals; i++) {
            final int threadNum = i + 1;
            String threadName = String.format("Withdraw-%02d", threadNum);
            Thread t = new Thread(() -> {
                ConsoleLogger.threadStarted(threadName);
                try {
                    transactionManager.executeTransaction(account, withdrawalAmount, "WITHDRAWAL");
                    ConsoleLogger.threadCompleted(threadName,
                            String.format("-$%,.2f → balance: $%,.2f", withdrawalAmount, account.getBalance()));
                } catch (InsufficientFundsException | OverdraftExceededException e) {
                    ConsoleLogger.threadFailed(threadName, e.getMessage());
                }
            }, threadName);
            threads.add(t);
        }

        // Start all threads and measure time
        long startTime = System.currentTimeMillis();
        ConsoleLogger.system("Launching all threads simultaneously...");
        System.out.println();

        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ConsoleLogger.error("Simulation interrupted: " + e.getMessage());
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;

        // Results
        System.out.println();
        ConsoleLogger.threadSummary(threads.size(), elapsed);
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println("SIMULATION RESULTS");
        System.out.println("─".repeat(60));
        System.out.printf("  Threads executed     : %d (%d deposits + %d withdrawals)%n",
                threads.size(), numDeposits, numWithdrawals);
        System.out.printf("  Time elapsed         : %dms%n", elapsed);
        System.out.printf("  Starting balance     : $%,.2f%n",
                expectedBalance - (numDeposits * depositAmount) + (numWithdrawals * withdrawalAmount));
        System.out.printf("  Final balance        : $%,.2f%n", account.getBalance());
        System.out.printf("  Expected balance     : $%,.2f%n", expectedBalance);

        double diff = Math.abs(account.getBalance() - expectedBalance);
        if (diff < 0.01) {
            ConsoleLogger.success("Thread-safe! No race conditions — balance matches expected value.");
        } else {
            ConsoleLogger.error("Race condition detected! Difference: $" + String.format("%,.2f", diff));
        }

        System.out.printf("  Total transactions   : %d%n", transactionManager.countByAccount(accountNumber));
        System.out.println("─".repeat(60));
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
            ConsoleLogger.error(e.getMessage());
            return;
        }

        System.out.printf("\nStarting balance: $%,.2f%n", account.getBalance());

        int batchSize = InputValidator.readInt(scanner, "Number of batch deposits (1-100): ", 1, 100);
        double amount = InputValidator.readDouble(scanner, "Amount per deposit: $");

        double expectedBalance = account.getBalance() + (batchSize * amount);

        System.out.println();
        ConsoleLogger.system("Processing " + batchSize + " deposits via parallel stream (ForkJoinPool)...");
        System.out.println("─".repeat(60));
        System.out.println();

        long startTime = System.currentTimeMillis();

        // Parallel stream processes deposits concurrently across multiple CPU cores
        List<String> results = IntStream.rangeClosed(1, batchSize)
                .parallel()
                .mapToObj(i -> {
                    String workerThread = Thread.currentThread().getName();
                    try {
                        transactionManager.executeTransaction(account, amount, "DEPOSIT");
                        return String.format("[OK]    Batch-%03d | +$%,.2f | balance: $%,.2f | thread: %s",
                                i, amount, account.getBalance(), workerThread);
                    } catch (InsufficientFundsException | OverdraftExceededException e) {
                        return String.format("[FAIL]  Batch-%03d | %s | thread: %s",
                                i, e.getMessage(), workerThread);
                    }
                })
                .collect(Collectors.toList());

        long elapsed = System.currentTimeMillis() - startTime;

        // Print results
        results.forEach(r -> System.out.println("  " + r));

        // Summary
        System.out.println();
        ConsoleLogger.threadSummary(batchSize, elapsed);
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println("PARALLEL STREAM RESULTS");
        System.out.println("─".repeat(60));
        System.out.printf("  Batch size           : %d deposits%n", batchSize);
        System.out.printf("  Time elapsed         : %dms%n", elapsed);
        System.out.printf("  Final balance        : $%,.2f%n", account.getBalance());
        System.out.printf("  Expected balance     : $%,.2f%n", expectedBalance);

        double diff = Math.abs(account.getBalance() - expectedBalance);
        if (diff < 0.01) {
            ConsoleLogger.success("Thread-safe! Parallel stream produced consistent results.");
        } else {
            ConsoleLogger.error("Inconsistency detected! Difference: $" + String.format("%,.2f", diff));
        }
        System.out.println("─".repeat(60));
    }
}
