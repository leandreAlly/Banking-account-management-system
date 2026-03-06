package com.leandre.cli;

public class MenuNavigation {
    public static void displayMainMenu() {
        System.out.println("+=========================================+");
        System.out.println("|   BANK ACCOUNT MANAGEMENT - MAIN MENU  |");
        System.out.println("+=========================================+");
        System.out.println();
        System.out.println("1. Create Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Process Transaction");
        System.out.println("4. View Transaction History");
        System.out.println("5. Exit");
        System.out.println();
        System.out.print("Enter choice: ");
    }
}
