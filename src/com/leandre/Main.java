package com.leandre;
import com.leandre.account.AccountManager;
import com.leandre.cli.AccountCLI;
import com.leandre.cli.MenuNavigation;
import com.leandre.cli.TransactionCLI;
import com.leandre.transaction.TransactionManager;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AccountManager accountManager = new AccountManager();
        TransactionManager transactionManager = new TransactionManager();
        int choice;

        do {
            MenuNavigation.displayMainMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1-5.");
                choice = -1;
            }

            switch (choice) {
                case 1:
                    System.out.println("→ Create Account selected");
                    AccountCLI.createAccount(accountManager);
                    break;
                case 2:
                    System.out.println("→ View Accounts selected");
                    AccountCLI.viewAccounts(scanner, accountManager);
                    break;
                case 3:
                    System.out.println("→ Process Transaction selected");
                    TransactionCLI.processTransaction(scanner, accountManager, transactionManager);
                    break;
                case 4:
                    System.out.println("→ View Transaction History selected");
                    TransactionCLI.viewTransactionHistory(scanner, transactionManager);
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-5.");
            }

        } while (choice != 5);

        scanner.close();
    }
}









