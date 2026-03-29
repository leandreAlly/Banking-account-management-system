package com.leandre;
import com.leandre.account.AccountManager;
import com.leandre.cli.AccountCLI;
import com.leandre.cli.MenuNavigation;
import com.leandre.cli.TransactionCLI;
import com.leandre.concurrent.ConcurrentTransactionSimulator;
import com.leandre.transaction.TransactionManager;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AccountManager accountManager = new AccountManager();
        TransactionManager transactionManager = new TransactionManager();
        int choice;

        accountManager.loadFromFile();

        do {
            MenuNavigation.displayMainMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1-6.");
                choice = -1;
            }

            switch (choice) {
                case 1:
                    System.out.println("→ Create Account selected");
                    AccountCLI.createAccount(accountManager);
                    accountManager.saveToFile();
                    break;
                case 2:
                    System.out.println("→ View Accounts selected");
                    AccountCLI.viewAccounts(scanner, accountManager);
                    break;
                case 3:
                    System.out.println("→ Process Transaction selected");
                    TransactionCLI.processTransaction(scanner, accountManager, transactionManager);
                    accountManager.saveToFile();
                    break;
                case 4:
                    System.out.println("→ View Transaction History selected");
                    TransactionCLI.viewTransactionHistory(scanner, accountManager, transactionManager);
                    break;
                case 5:
                    System.out.println("→ Concurrent Transaction Simulation selected");
                    System.out.println("\nSimulation type:");
                    System.out.println("1. Thread Simulation (manual threads with deposits & withdrawals)");
                    System.out.println("2. Parallel Stream Simulation (batch deposits)");
                    int simChoice = Integer.parseInt(scanner.nextLine().trim());
                    if (simChoice == 1) {
                        ConcurrentTransactionSimulator.runThreadSimulation(scanner, accountManager, transactionManager);
                    } else if (simChoice == 2) {
                        ConcurrentTransactionSimulator.runParallelStreamSimulation(scanner, accountManager, transactionManager);
                    } else {
                        System.out.println("Invalid simulation type.");
                    }
                    accountManager.saveToFile();
                    System.out.print("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case 6:
                    accountManager.saveToFile();
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-6.");
            }

        } while (choice != 6);

        scanner.close();
    }
}









