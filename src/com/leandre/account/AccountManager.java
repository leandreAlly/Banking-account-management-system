package com.leandre.account;

import com.leandre.customer.Customer;
import com.leandre.customer.PremiumCustomer;
import com.leandre.customer.RegularCustomer;
import com.leandre.exception.InvalidAccountException;

public class AccountManager {
    private Account[] accounts;
    private int accountCount;

    public AccountManager() {
        accounts = new Account[50];
        accountCount =  0;
    }

    public Customer createCustomer(int customerType, String name, int age, String contact, String address) {
        String customerId = "CUST-" + (customerType == 2 ? "PREM" : "REG") + "-" + String.format("%03d", Account.getAccountCounter() + 1);
        if (customerType == 2) {
            return new PremiumCustomer(customerId, name, age, contact, address);
        }
        return new RegularCustomer(customerId, name, age, contact, address);
    }

    public Account createAccount(int accountType, double initialDeposit, Customer customer) {
        String accountNumber = "ACC-" + String.format("%03d", Account.getAccountCounter() + 1);
        Account account;
        if (accountType == 1) {
            account = new SavingAccount(accountNumber, initialDeposit, "Active", customer);
        } else {
            account = new CheckingAccount(accountNumber, initialDeposit, "Active", customer);
        }
        addAccount(account);
        return account;
    }

    public void addAccount(Account account) {
        if (accountCount < accounts.length) {
            accounts[accountCount++] = account;
            System.out.println("Account added successfully: " + account.getAccountNumber());
        } else {
            System.out.println("Account limit reached. Cannot add more accounts.");
        }
    }

    public Account findAccount(String accountNumber) throws InvalidAccountException {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        throw new InvalidAccountException("Account not found, Please check account number and try again: " + accountNumber, accountNumber);
    }

    public void viewAllAccounts() {
        if (accountCount == 0) {
            System.out.println("No accounts to display.");
            return;
        }
        for (int i = 0; i < accountCount; i++) {
            accounts[i].displayAccountDetails();
            System.out.println("-----------------------------");
        }
    }

    public void getTotalBalance(){
        double totalBalance = 0;
        for (int i = 0; i < accountCount; i++) {
            totalBalance += accounts[i].getBalance();
        }
        System.out.println("Total Balance across all accounts: $" + totalBalance);
    }

    public void getAccountCount() {
        System.out.println("Total number of accounts: " + accountCount);
    }
}
