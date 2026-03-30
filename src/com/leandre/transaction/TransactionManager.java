package com.leandre.transaction;

import com.leandre.account.Account;
import com.leandre.cli.ConsoleLogger;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.OverdraftExceededException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionManager {
    //  private final List<Transaction> transactions = new ArrayList<>();
    private final List<Transaction> transactions;

    public TransactionManager() {
        transactions = Collections.synchronizedList(new ArrayList<>());
    }

    // synchronized on both accounts (ordered by account number to prevent deadlocks)
    // ensures the transfer is atomic: source debit + destination credit happen together
    public Transaction[] executeTransfer(Account source, Account destination, double amount) throws InsufficientFundsException, OverdraftExceededException {
        // Lock accounts in consistent order to prevent deadlock (A->B vs B->A)
        Account first = source.getAccountNumber().compareTo(destination.getAccountNumber()) < 0 ? source : destination;
        Account second = first == source ? destination : source;

        synchronized (first) {
            synchronized (second) {
                source.processTransaction(amount, "TRANSFER_OUT");
                destination.processTransaction(amount, "TRANSFER_IN");

                Transaction outTransaction = new Transaction(source.getAccountNumber(), "TRANSFER_OUT", amount, source.getBalance());
                Transaction inTransaction = new Transaction(destination.getAccountNumber(), "TRANSFER_IN", amount, destination.getBalance());

                addTransaction(outTransaction);
                addTransaction(inTransaction);

                return new Transaction[]{outTransaction, inTransaction};
            }
        }
    }

    public Transaction executeTransaction(Account account, double amount, String type) throws InsufficientFundsException, OverdraftExceededException {
        // Account methods are already synchronized, but we synchronize here too
        // to ensure the balance read for the Transaction record is consistent
        synchronized (account) {
            account.processTransaction(amount, type);
            Transaction transaction = new Transaction(account.getAccountNumber(), type, amount, account.getBalance());
            addTransaction(transaction);
            return transaction;
        }
    }

    public void addTransaction(Transaction transaction) {
        // synchronizedList handles the thread-safety of add()
        transactions.add(transaction);
        ConsoleLogger.txnAdded(
                transaction.getTransactionId(),
                transaction.getAccountNumber(),
                transaction.getType(),
                transaction.getAmount());
    }

    // NOTE: Collections.synchronizedList requires manual synchronization for
    //        iteration and stream operations. All stream calls below use
    //        synchronized(transactions) to prevent ConcurrentModificationException.

    public String viewTransactionByAccount(String accountNumber) {
        synchronized (transactions) {
            String result = transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .map(t -> "Transaction ID: " + t.getTransactionId()
                            + ", Type: " + t.getType()
                            + ", Amount: " + t.getAmount()
                            + ", Balance After: " + t.getBalanceAfter()
                            + ", Timestamp: " + t.getFormattedTimestamp())
                    .collect(Collectors.joining("\n"));

            return result.isEmpty() ? "No transactions found for account: " + accountNumber : result;
        }
    }

    public double calculateTotalDeposits(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .filter(t -> t.getType().equals("DEPOSIT") || t.getType().equals("TRANSFER_IN"))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }
    }

    public double calculateTotalWithdrawals(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .filter(t -> t.getType().equals("WITHDRAWAL") || t.getType().equals("TRANSFER_OUT"))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }
    }

    public int countByAccount(String accountNumber) {
        synchronized (transactions) {
            return (int) transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .count();
        }
    }

    public List<Transaction> getByAccount(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .collect(Collectors.toList());
        }
    }

    public List<Transaction> getByAccountSortedByDateDesc(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                    .collect(Collectors.toList());
        }
    }

    public List<Transaction> getByAccountSortedByDateAsc(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .sorted(Comparator.comparing(Transaction::getTimestamp))
                    .collect(Collectors.toList());
        }
    }

    public List<Transaction> getByAccountSortedByAmountDesc(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .sorted(Comparator.comparingDouble(Transaction::getAmount).reversed())
                    .collect(Collectors.toList());
        }
    }

    public List<Transaction> getByAccountSortedByAmountAsc(String accountNumber) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .sorted(Comparator.comparingDouble(Transaction::getAmount))
                    .collect(Collectors.toList());
        }
    }

    public List<Transaction> getByAccountAndType(String accountNumber, String type) {
        synchronized (transactions) {
            return transactions.stream()
                    .filter(t -> t.getAccountNumber().equals(accountNumber))
                    .filter(t -> t.getType().equals(type))
                    .collect(Collectors.toList());
        }
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public Transaction getTransaction(int index) {
        return transactions.get(index);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}