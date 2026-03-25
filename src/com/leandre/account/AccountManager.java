package com.leandre.account;

import com.leandre.customer.Customer;
import com.leandre.customer.PremiumCustomer;
import com.leandre.customer.RegularCustomer;
import com.leandre.exception.InvalidAccountException;

import com.leandre.cli.ConsoleLogger;
import com.leandre.storage.AccountFileManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    // private Account[] accounts;
    // private int accountCount;

    private final Map<String, Account> accounts;
    private final AccountFileManager fileManager;

    public AccountManager() {
        //  accounts = new Account[50];
        // accountCount = 0;
        accounts = new HashMap<>();
        fileManager = new AccountFileManager();
    }

    public Customer createCustomer(int customerType, String name, int age, String contact, String email, String address) {
        String customerId = "CUST-" + (customerType == 2 ? "PREM" : "REG") + "-" + String.format("%03d", Account.getAccountCounter() + 1);
        if (customerType == 2) {
            return new PremiumCustomer(customerId, name, age, contact, email, address);
        }
        return new RegularCustomer(customerId, name, age, contact, email, address);
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
        // checked if accountCount < accounts.length (max 50)
        // if (accountCount < accounts.length) {
        //     accounts[accountCount++] = account;
        //     System.out.println("Account added successfully: " + account.getAccountNumber());
        // } else {
        //     System.out.println("Account limit reached. Cannot add more accounts.");
        // }

        if (accounts.containsKey(account.getAccountNumber())) {
            ConsoleLogger.warn("Account already exists: " + account.getAccountNumber());
            return;
        }
        accounts.put(account.getAccountNumber(), account);
        ConsoleLogger.success("Account created: " + account.getAccountNumber()
                + " | " + account.getAccountType()
                + " | " + account.getCustomer().getName()
                + " | $" + String.format("%,.2f", account.getBalance()));
    }

    public Account findAccount(String accountNumber) throws InvalidAccountException {
        // O(n) linear search through the array
        // for (int i = 0; i < accountCount; i++) {
        //     if (accounts[i].getAccountNumber().equals(accountNumber)) {
        //         return accounts[i];
        //     }
        // }

        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new InvalidAccountException("Account not found, Please check account number and try again: " + accountNumber, accountNumber);
        }
        return account;
    }

    public void viewAllAccounts() {
        //  if (accountCount == 0)
        //  for (int i = 0; i < accountCount; i++) { accounts[i].displayAccountDetails(); }
        if (accounts.isEmpty()) {
            System.out.println("No accounts to display.");
            return;
        }
        for (Account account : accounts.values()) {
            account.displayAccountDetails();
            System.out.println("-----------------------------");
        }
    }

    public void getTotalBalance(){
        double totalBalance = 0;
        // for (int i = 0; i < accountCount; i++) { totalBalance += accounts[i].getBalance(); }
        for (Account account : accounts.values()) {
            totalBalance += account.getBalance();
        }
        System.out.println("Total Balance across all accounts: $" + totalBalance);
    }

    public void getAccountCount() {
        //System.out.println("Total number of accounts: " + accountCount);
        System.out.println("Total number of accounts: " + accounts.size());
    }


    public void saveToFile() {
        try {
            fileManager.saveAccounts(accounts);
            ConsoleLogger.success("All account data persisted successfully");
        } catch (IOException e) {
            ConsoleLogger.fileError("save", e.getMessage());
        }
    }

    // Load accounts from file (uses Files.lines() + stream mapping)
    public void loadFromFile() {
        try {
            Map<String, Account> loaded = fileManager.loadAccounts();
            accounts.putAll(loaded);
            if (!loaded.isEmpty()) {
                ConsoleLogger.success("Accounts ready — " + loaded.size() + " loaded from disk");
            }
        } catch (IOException e) {
            ConsoleLogger.fileError("load", e.getMessage());
        }
    }
}