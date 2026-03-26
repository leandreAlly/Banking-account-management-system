package com.leandre.account;

public class AccountManager {
    private Account[] accounts;
    private int accountCount;

    public AccountManager() {
        accounts = new Account[50];
        accountCount =  0;
    }

    public void addAccount(Account account) {
        if (accountCount < accounts.length) {
            accounts[accountCount++] = account;
            System.out.println("Account added successfully: " + account.getAccountNumber());
        } else {
            System.out.println("Account limit reached. Cannot add more accounts.");
        }
    }

    public Account findAccount(String accountNumber) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        return null;
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
