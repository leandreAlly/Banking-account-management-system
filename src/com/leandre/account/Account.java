package com.leandre.account;

import com.leandre.customer.Customer;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.OverdraftExceededException;
import com.leandre.transaction.Transactable;

public abstract class Account implements Transactable {

    @Override
    public boolean processTransaction(double amount, String type) throws InsufficientFundsException, OverdraftExceededException {
        if (type.equals("DEPOSIT") || type.equals("TRANSFER_IN")) {
            deposit(amount);
        } else if (type.equals("WITHDRAWAL") || type.equals("TRANSFER_OUT")) {
            withdraw(amount);
        } else {
            return false;
        }
        return true;
    }
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
    public abstract double withdraw(double amount) throws InsufficientFundsException, OverdraftExceededException;

    public double previewBalance(double amount, String type) {
        if (type.equals("DEPOSIT") || type.equals("TRANSFER_IN")) {
            return balance + amount;
        } else {
            return balance - amount;
        }
    }

    protected void validateAmount(double amount) {
        //TODO: move this to invalid amount custom exception
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    }

    public double deposit(double amount)
    {
        validateAmount(amount);
        balance = balance + amount;
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

    protected void setBalance(double balance) {
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


