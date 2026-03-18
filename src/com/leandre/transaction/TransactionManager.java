package com.leandre.transaction;

import com.leandre.account.Account;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.OverdraftExceededException;

public class TransactionManager {
    private final Transaction[] transactions;
    private int transactionCount;

    public TransactionManager() {
        transactions = new Transaction[100];
        transactionCount = 0;
    }

    public Transaction[] executeTransfer(Account source, Account destination, double amount) throws InsufficientFundsException, OverdraftExceededException {
        source.processTransaction(amount, "TRANSFER_OUT");
        destination.processTransaction(amount, "TRANSFER_IN");

        Transaction outTransaction = new Transaction(source.getAccountNumber(), "TRANSFER_OUT", amount, source.getBalance());
        Transaction inTransaction = new Transaction(destination.getAccountNumber(), "TRANSFER_IN", amount, destination.getBalance());

        addTransaction(outTransaction);
        addTransaction(inTransaction);

        return new Transaction[]{outTransaction, inTransaction};
    }

    public Transaction executeTransaction(Account account, double amount, String type) throws InsufficientFundsException, OverdraftExceededException {
        account.processTransaction(amount, type);
        Transaction transaction = new Transaction(account.getAccountNumber(), type, amount, account.getBalance());
        addTransaction(transaction);
        return transaction;
    }

    public void addTransaction(Transaction transaction) {
        if (transactionCount < transactions.length) {
            transactions[transactionCount++] = transaction;
            System.out.println("Transaction added successfully: " + transaction.getTransactionId());
        } else {
            System.out.println("Transaction limit reached. Cannot add more transactions.");
        }
    }

    public String viewTransactionByAccount(String accountNumber) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < transactionCount; i++) {
            Transaction t = transactions[i];
            if (t.getAccountNumber().equals(accountNumber)) {
                result.append("Transaction ID: ").append(t.getTransactionId())
                        .append(", Type: ").append(t.getType())
                        .append(", Amount: ").append(t.getAmount())
                        .append(", Balance After: ").append(t.getBalanceAfter())
                        .append(", Timestamp: ").append(t.getTimestamp())
                        .append("\n");
            }
        }

        return result.isEmpty() ? "No transactions found for account: " + accountNumber : result.toString();
    }

    public double calculateTotalDeposits(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            Transaction t = transactions[i];
            if (t.getAccountNumber().equals(accountNumber) && (t.getType().equals("DEPOSIT") || t.getType().equals("TRANSFER_IN"))) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double calculateTotalWithdrawals(String accountNumber) {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            Transaction t = transactions[i];
            if (t.getAccountNumber().equals(accountNumber) && (t.getType().equals("WITHDRAWAL") || t.getType().equals("TRANSFER_OUT"))) {
                total += t.getAmount();
            }
        }
        return total;
    }
    public int countByAccount(String accountNumber) {
        int count = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getAccountNumber().equals(accountNumber)) count++;
        }
        return count;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public Transaction getTransaction(int index) {
        if (index < 0 || index >= transactionCount) {
            throw new IndexOutOfBoundsException("Invalid transaction index: " + index);
        }
        return transactions[index];
    }
}