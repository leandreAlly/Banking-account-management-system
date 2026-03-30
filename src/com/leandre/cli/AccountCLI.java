package com.leandre.cli;

import com.leandre.account.Account;
import com.leandre.account.AccountManager;
import com.leandre.customer.Customer;
import com.leandre.validation.InputValidator;

import java.util.Scanner;

public class AccountCLI {
    public static Scanner scanner = new Scanner(System.in);

    // Method to handle account creation
    public static void createAccount(AccountManager accountManager) {
        System.out.println("ACCOUNT CREATION");
        System.out.println("----------------------------------------");

        String name = InputValidator.readName(scanner, "Enter customer name: ");
        int age = InputValidator.readAge(scanner, "Enter customer age: ");
        String contact = InputValidator.readContact(scanner, "Enter customer contact: ");
        String email = InputValidator.readEmail(scanner, "Enter customer email: ");
        String address = InputValidator.readAddress(scanner, "Enter customer address: ");

        // Customer type
        System.out.println("\nCustomer type:");
        System.out.println("1. Regular Customer (Standard banking services)");
        System.out.println("2. Premium Customer (Enhanced benefits, min balance $10,000)");
        int customerType = InputValidator.readInt(scanner, "Select type (1-2): ", 1, 2);

        // Account type
        System.out.println("\nAccount type:");
        System.out.println("1. Savings Account (Interest: 3.5%, Min Balance: $500)");
        System.out.println("2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");
        int accountType = InputValidator.readInt(scanner, "Select type (1-2): ", 1, 2);

        double initialDeposit = InputValidator.readDouble(scanner, "Enter initial deposit amount: $");

        // Delegate creation to AccountManager
        Customer customer = accountManager.createCustomer(customerType, name, age, contact, email, address);
        Account account = accountManager.createAccount(accountType, initialDeposit, customer);

        // Display summary
        System.out.println("\n✓ Account created successfully!");
        account.displayAccountDetails();

        System.out.print("\nPress Enter to continue...");
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