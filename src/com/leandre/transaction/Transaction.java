package com.leandre.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private String accountNumber;
    private String type;
    private double amount;
    private double balanceAfter;
    private String timestamp;

    private static int transactionCounter = 0;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        this.transactionId = "TXN" + String.format("%03d", ++transactionCounter);
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public void displayTransactionDetails() {
        System.out.println("Transaction ID  : " + transactionId);
        System.out.println("Account Number  : " + accountNumber);
        System.out.println("Type            : " + type);
        System.out.println("Amount          : " + amount);
        System.out.println("Balance After   : " + balanceAfter);
        System.out.println("Timestamp       : " + timestamp);
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static String peekNextId() {
        return "TXN" + String.format("%03d", transactionCounter + 1);
    }

    public static int getTransactionCounter() {
        return transactionCounter;
    }

    public static void setTransactionCounter(int transactionCounter) {
        Transaction.transactionCounter = transactionCounter;
    }
}
