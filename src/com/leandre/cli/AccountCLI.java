package com.leandre.cli;

import com.leandre.account.Account;
import com.leandre.account.AccountManager;
import com.leandre.account.CheckingAccount;
import com.leandre.account.SavingAccount;
import com.leandre.customer.Customer;
import com.leandre.customer.PremiumCustomer;
import com.leandre.customer.RegularCustomer;

import java.util.Scanner;

public class AccountCLI {
    public static Scanner scanner = new Scanner(System.in);

    // Method to handle account creation
    public static void createAccount(AccountManager accountManager) {
        System.out.println("ACCOUNT CREATION");
        System.out.println("----------------------------------------");


        scanner.nextLine();
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter customer age: ");
        int age = scanner.nextInt();

        scanner.nextLine();
        System.out.print("Enter customer contact: ");
        String contact = scanner.nextLine();

        System.out.print("Enter customer address: ");
        String address = scanner.nextLine();

        // Customer type
        System.out.println("\nCustomer type:");
        System.out.println("1. Regular Customer (Standard banking services)");
        System.out.println("2. Premium Customer (Enhanced benefits, min balance $10,000)");
        System.out.print("Select type (1-2): ");
        int customerType = scanner.nextInt();

        // Account type
        System.out.println("\nAccount type:");
        System.out.println("1. Savings Account (Interest: 3.5%, Min Balance: $500)");
        System.out.println("2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");
        System.out.print("Select type (1-2): ");
        int accountType = scanner.nextInt();

        System.out.print("Enter initial deposit amount: $");
        double initialDeposit = scanner.nextDouble();

        // Build the customer
        String customerId = "CUST-" + (customerType == 2 ? "PREM" : "REG") + "-" + (Account.getAccountCounter() + 1);
        Customer customer;
        if (customerType == 2) {
            customer = new PremiumCustomer(customerId, name, age, contact, address);
        } else {
            customer = new RegularCustomer(customerId, name, age, contact, address);
        }

        // Build the account and hand it off to AccountManager
        String accountNumber = "ACC-" + (Account.getAccountCounter() + 1);
        Account account;
        if (accountType == 1) {
            account = new SavingAccount(accountNumber, initialDeposit, "Active", customer);
        } else {
            account = new CheckingAccount(accountNumber, initialDeposit, "Active", customer);
        }

        accountManager.addAccount(account);

        // Display summary
        System.out.println("\n✓ Account created successfully!");
        account.displayAccountDetails();

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }

    // Method to handle viewing accounts
    public static void viewAccounts(Scanner scanner, AccountManager accountManager) {
        System.out.println("\nACCOUNT LISTING");
        System.out.println("─".repeat(70));
        System.out.printf("%-8s | %-18s | %-12s | %12s | %s%n",
                "ACC NO", "CUSTOMER NAME", "TYPE", "BALANCE", "STATUS");
        System.out.println("─".repeat(70));

        accountManager.viewAllAccounts();

        System.out.println();
        accountManager.getAccountCount();
        accountManager.getTotalBalance();

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }



}
