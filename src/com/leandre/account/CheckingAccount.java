package com.leandre.account;

import com.leandre.customer.Customer;
import com.leandre.exception.OverdraftExceededException;

public class CheckingAccount extends Account {
    private static final double DEFAULT_OVERDRAFT_LIMIT = 1000;
    private static final double DEFAULT_MONTHLY_FEE = 10;

    private double overdraftLimit;
    private double monthlyFee;

    public CheckingAccount(String accountNumber, double balance, String status, Customer customer) {
        this(accountNumber, balance, status, customer, DEFAULT_OVERDRAFT_LIMIT, DEFAULT_MONTHLY_FEE);
    }

    public CheckingAccount(String accountNumber, double balance, String status, Customer customer,
                           double overdraftLimit, double monthlyFee) {
        super(accountNumber, balance, status, customer);
        this.overdraftLimit = overdraftLimit;
        this.monthlyFee = monthlyFee;
    }

    @Override
    public void displayAccountDetails() {
        System.out.printf("%-8s | %-18s | %-12s | %12s | %s%n",
                getAccountNumber(),
                getCustomer().getName(),
                "Checking",
                String.format("$%,.2f", getBalance()),
                getStatus());
        System.out.printf("         | Overdraft Limit: $%,.2f | Monthly Fee: $%,.2f%n",
                overdraftLimit, monthlyFee);
        System.out.println("─".repeat(70));
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }


    @Override
    public synchronized double withdraw(double amount) throws OverdraftExceededException {
        validateAmount(amount);
        if (amount > getBalance() + overdraftLimit) {
            throw new OverdraftExceededException(
                    "Amount exceeds balance + overdraft limit of $" + String.format("%,.2f", overdraftLimit),
                    getBalance(), amount, overdraftLimit);
        }
        setBalance(getBalance() - amount);
        return getBalance();
    }


    public void applyMonthlyFee() {
        //TODO: Handle the case when balance is less than monthly fee
        setBalance(getBalance() - monthlyFee);
        System.out.println("Monthly fee of $" + monthlyFee + " applied. New balance: $" + getBalance());
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }

    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }
}