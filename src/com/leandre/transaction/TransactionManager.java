package com.leandre.transaction;

public class TransactionManager {
    private final Transaction[] transactions;
    private int transactionCount;

    public TransactionManager() {
        transactions = new Transaction[100];
        transactionCount = 0;
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

    public void calculateTotalDeposits(String accountNumber) {
        double totalDeposits = 0;

        for (int i = 0; i < transactionCount; i++) {
            Transaction t = transactions[i];
            //TODO: check if I need to specify the account type
            if (t.getAccountNumber().equals(accountNumber)) {
                totalDeposits += t.getAmount();
            }
        }

        System.out.println("Total deposits for account " + accountNumber + ": " + totalDeposits);
    }

    public void calculateTotalWithdrawals(String accountNumber) {
        double totalWithdrawals = 0;

        for (int i = 0; i < transactionCount; i++) {
            Transaction t = transactions[i];
            //TODO: check if I need to specify the account type
            if (t.getAccountNumber().equals(accountNumber)) {
                totalWithdrawals += t.getAmount();
            }

        }

        System.out.println("Total withdrawals for account " + accountNumber + ": " + totalWithdrawals);
    }

    public int getTransactionCount() {
        return transactionCount;
    }
}