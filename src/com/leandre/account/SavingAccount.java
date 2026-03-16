package com.leandre.account;

import com.leandre.customer.Customer;
import com.leandre.exception.InsufficientFundsException;

public class SavingAccount extends Account {
    private static final double DEFAULT_INTEREST_RATE = 3.5;
    private static final double DEFAULT_MINIMUM_BALANCE = 500;

    private final double interestRate;
    private final double minimumBalance;

    public SavingAccount(String accountNumber, double balance, String status, Customer customer) {
        this(accountNumber, balance, status, customer, DEFAULT_INTEREST_RATE, DEFAULT_MINIMUM_BALANCE);
    }

    public SavingAccount(String accountNumber, double balance, String status, Customer customer,
                         double interestRate, double minimumBalance) {
        super(accountNumber, balance, status, customer);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
    }


    @Override
    public void displayAccountDetails() {
        System.out.printf("%-8s | %-18s | %-12s | %12s | %s%n",
                getAccountNumber(),
                getCustomer().getName(),
                "Savings",
                String.format("$%,.2f", getBalance()),
                getStatus());
        System.out.printf("         | Interest Rate: %.1f%%   | Min Balance: $%,.2f%n",
                interestRate, minimumBalance);
        System.out.println("─".repeat(70));
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    @Override
    public double withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (getBalance() - amount < minimumBalance) {
            throw new InsufficientFundsException(
                    "Withdrawal denied. Balance cannot fall below the minimum balance of $" +
                    String.format("%,.2f", minimumBalance),
                    getBalance(), amount);
        }
        setBalance(getBalance() - amount);
        return getBalance();
    }

    public double calculateInterestRate() {
        return getBalance() * (interestRate / 100);
    }
}

