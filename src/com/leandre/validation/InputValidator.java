package com.leandre.validation;

import java.util.Scanner;

public class InputValidator {

    // Validate and read account number using ValidationUtils regex (Pattern + Matcher)
    public static String readAccountNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) {
                System.out.println("✗ Account number cannot be empty.");
                continue;
            }
            // Uses Predicate from ValidationUtils
            if (!ValidationUtils.isValidAccountNumber.test(input)) {
                System.out.println("✗ " + ValidationUtils.getAccountNumberError());
                continue;
            }
            return input;
        }
    }

    // Validate and read email using ValidationUtils regex (Pattern + Matcher)
    public static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("✗ Email cannot be empty.");
                continue;
            }
            // Uses Pattern/Matcher method from ValidationUtils
            if (!ValidationUtils.validateEmail(input)) {
                System.out.println("✗ " + ValidationUtils.getEmailError());
                continue;
            }
            return input;
        }
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("✗ Please enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    System.out.println("✗ Amount must be positive.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid amount.");
            }
        }
    }

    public static String readString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("✗ This field cannot be empty.");
                continue;
            }
            return input;
        }
    }

    public static String readName(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("✗ Name cannot be empty.");
                continue;
            }
            if (!input.matches("[a-zA-Z ]+")) {
                System.out.println("✗ Name must contain only letters and spaces.");
                continue;
            }
            return input;
        }
    }

    public static int readAge(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int age = Integer.parseInt(input);
                if (age < 18 || age > 120) {
                    System.out.println("✗ Age must be between 18 and 120.");
                    continue;
                }
                return age;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid age.");
            }
        }
    }

    public static String readContact(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("✗ Contact cannot be empty.");
                continue;
            }
            if (!input.matches("[0-9+\\-() ]+")) {
                System.out.println("✗ Contact must contain only digits, +, -, (, ) and spaces.");
                continue;
            }
            return input;
        }
    }

    public static String readAddress(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("✗ Address cannot be empty.");
                continue;
            }
            if (input.matches("[0-9]+")) {
                System.out.println("✗ Address cannot be only numbers.");
                continue;
            }
            return input;
        }
    }

    public static String readConfirmation(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("N")) {
                return input;
            }
            System.out.println("✗ Please enter Y or N.");
        }
    }
}