package com.leandre.account;

import com.leandre.customer.Customer;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private String status;
    private Customer customer;
    private static int accountCounter;

    public Account(String accountNumber, double balance, String status, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.customer = customer;
        accountCounter++;
    }

    public abstract void displayAccountDetails();
    public abstract String getAccountType();

    public double deposit(double amount) {
        if(amount <= 0) throw new IllegalArgumentException("Amount cannot be negative");
        balance = balance + amount;
        return balance;
    }
    public double withdraw(double amount) {
        if(amount <= 0) throw new IllegalArgumentException("Amount cannot be negative");
        if (amount > balance) throw new IllegalArgumentException("Amount cannot be greater than account's balance");
        balance = balance - amount;
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getAccountCounter() {
        return accountCounter;
    }

    public static void setAccountCounter(int accountCounter) {
        Account.accountCounter = accountCounter;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
