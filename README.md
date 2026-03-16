# 🏦 Bank Account Manager

A simple command-line banking application built with Java. It allows users to create customer accounts, make deposits and withdrawals, and view transaction history.

---

## Features

- Create **Savings** or **Checking** accounts linked to a customer
- Support for **Regular** and **Premium** customer types
- Deposit and withdraw funds (with balance validation)
- View all accounts and their details
- Track and view transaction history per account

---

## Project Structure

```
src/com/leandre/
├── Main.java                   # Entry point
├── account/
│   ├── Account.java            # Abstract base account class
│   ├── AccountManager.java     # Manages the list of accounts
│   ├── CheckingAccount.java    # Checking account implementation
│   └── SavingAccount.java      # Savings account implementation
├── cli/
│   ├── AccountCLI.java         # CLI for account creation & viewing
│   ├── MenuNavigation.java     # Main menu display
│   └── TransactionCLI.java     # CLI for transactions & history
├── customer/
│   ├── Customer.java           # Abstract base customer class
│   ├── PremiumCustomer.java    # Premium customer type
│   └── RegularCustomer.java    # Regular customer type
└── transaction/
    ├── Transactable.java       # Transaction interface
    ├── Transaction.java        # Transaction model
    └── TransactionManager.java # Manages transaction records
```

---

## Getting Started

### Requirements

- Java 17 or higher
- Any Java IDE (IntelliJ IDEA recommended) or the terminal

### Run the App

1. Clone or download the project
2. Open it in your IDE and run `Main.java`, **or** compile and run from the terminal:

```bash
javac -d out src/com/leandre/**/*.java src/com/leandre/Main.java
java -cp out com.leandre.Main
```

---

## Usage

When the app starts, you'll see the main menu:

```
========================================
         BANK ACCOUNT MANAGER
========================================
  1. Create Account
  2. View Accounts
  3. Process Transaction
  4. View Transaction History
  5. Exit
========================================
```

### Creating an Account

- Enter customer details (name, age, contact, address)
- Choose customer type: **Regular** or **Premium**
- Choose account type: **Savings** or **Checking**
- Enter an initial deposit amount

Account numbers are auto-generated in the format `ACC-001`, `ACC-002`, etc.

### Account Types

| Type     | Details                                      |
|----------|----------------------------------------------|
| Savings  | Interest rate: 3.5%, Minimum balance: $500   |
| Checking | Overdraft limit: $1,000, Monthly fee: $10    |

### Customer Types

| Type    | Details                                         |
|---------|-------------------------------------------------|
| Regular | Standard banking services                       |
| Premium | Enhanced benefits, minimum balance of $10,000   |

---

## Author

Leandre

