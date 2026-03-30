# Bank Account Manager

A command-line banking application built with Java. Supports account management, transactions, file persistence, input validation, and thread-safe concurrent operations.

---

## Features

- Create **Savings** or **Checking** accounts linked to a customer
- Support for **Regular** and **Premium** customer types
- Deposit, withdraw, and transfer funds (with balance validation)
- View all accounts and their details
- Track and view transaction history per account
- **Sort & filter** transaction history by date, amount, or type (using Java Streams)
- **File persistence** — accounts save to disk and reload on startup (using `java.nio.file.Files`)
- **Regex validation** — account numbers (`ACC-\d{3}`) and emails validated with `Pattern`/`Matcher`/`Predicate`
- **Thread-safe concurrency** — `synchronized` methods and `AtomicInteger` prevent race conditions
- **Concurrent simulation** — run multi-threaded deposits/withdrawals or parallel stream batch operations
- **Console logging** — timestamped, tagged log output for file, thread, transaction, and system events

---

## Project Structure

```
src/com/leandre/
├── Main.java                          # Entry point with startup/shutdown logging
├── account/
│   ├── Account.java                   # Abstract base class (synchronized balance ops)
│   ├── AccountManager.java            # HashMap-based account storage + file persistence
│   ├── CheckingAccount.java           # Checking account (overdraft, monthly fee)
│   └── SavingAccount.java             # Savings account (interest, minimum balance)
├── cli/
│   ├── AccountCLI.java                # CLI for account creation & viewing
│   ├── ConsoleLogger.java             # Timestamped tagged logging ([FILE], [THREAD], [TXN], etc.)
│   ├── MenuNavigation.java            # Main menu display
│   └── TransactionCLI.java            # CLI for transactions, history, sorting & filtering
├── concurrent/
│   └── ConcurrentTransactionSimulator.java  # Thread & parallel stream simulation
├── customer/
│   ├── Customer.java                  # Abstract base customer (with email field)
│   ├── PremiumCustomer.java           # Premium customer ($10,000 min balance, fee waiver)
│   └── RegularCustomer.java           # Regular customer
├── exception/
│   ├── InsufficientFundsException.java
│   ├── InvalidAccountException.java
│   └── OverdraftExceededException.java
├── storage/
│   └── AccountFileManager.java        # File read/write using Files.lines() and streams
├── transaction/
│   ├── Transactable.java              # Transaction interface
│   ├── Transaction.java               # Transaction model (AtomicInteger ID, LocalDateTime)
│   └── TransactionManager.java        # Thread-safe transaction storage (synchronizedList)
└── validation/
    ├── InputValidator.java            # CLI input readers with retry loops
    └── ValidationUtils.java           # Regex patterns, matchers, and predicates

test/com/leandre/account/
└── AccountTest.java                   # JUnit 5 tests (deposit, withdraw, transfer, overdraft)
```

---

## Getting Started

### Requirements

- Java 17 or higher
- JUnit 5 (included in `lib/` for tests)

### Run the App

```bash
# Compile
javac -d out src/com/leandre/exception/*.java src/com/leandre/validation/*.java \
  src/com/leandre/transaction/*.java src/com/leandre/customer/*.java \
  src/com/leandre/storage/*.java src/com/leandre/account/*.java \
  src/com/leandre/concurrent/*.java src/com/leandre/cli/*.java \
  src/com/leandre/Main.java

# Run
java -cp out com.leandre.Main
```

### Run Tests

```bash
./run-tests.sh
```

---

## Usage

When the app starts, saved accounts are loaded from `accounts.txt` with timestamped log output:

```
+==============================================+
|     BANK ACCOUNT MANAGEMENT SYSTEM v2.0     |
|     Thread-Safe | File-Persistent | CLI      |
+==============================================+
[14:32:05.123] [SYSTEM] Application starting...
[14:32:05.200] [FILE]   Reading accounts from accounts.txt...
[14:32:05.210] [FILE]   Loaded 2 account(s) from accounts.txt
[14:32:05.211] [FILE]     Restored: ACC-001 | John | Savings | $1,500.00
[14:32:05.212] [FILE]     Restored: ACC-002 | Jane | Checking | $3,000.00
[14:32:05.213] [OK]     Accounts ready — 2 loaded from disk
```

### Main Menu

```
+=========================================+
|   BANK ACCOUNT MANAGEMENT - MAIN MENU  |
+=========================================+

1. Create Account
2. View Accounts
3. Process Transaction
4. View Transaction History
5. Concurrent Transaction Simulation
6. Exit
```

### Creating an Account

- Enter customer details: name, age, contact, **email** (regex-validated), address
- Choose customer type: **Regular** or **Premium**
- Choose account type: **Savings** or **Checking**
- Enter an initial deposit amount
- Account is auto-saved to file after creation

### Transaction History (Sort & Filter)

When viewing transaction history, choose from:
1. Default (chronological order)
2. Sort by Date (newest/oldest first)
3. Sort by Amount (highest/lowest first)
4. Filter by Type (deposits only / withdrawals only)

### Concurrent Transaction Simulation

Two modes demonstrate thread-safety:

**Thread Simulation** — launches N deposit + N withdrawal threads simultaneously:
```
[14:35:10.100] [THREAD] Deposit-01 started
[14:35:10.101] [THREAD] Withdraw-01 started
[14:35:10.102] [TXN]    TXN005 | ACC-001 | DEPOSIT      | $100.00
[14:35:10.103] [THREAD] Deposit-01 completed — +$100.00 → balance: $1,600.00
[14:35:10.150] [THREAD] All 4 threads finished in 50ms
[14:35:10.151] [OK]     Thread-safe! No race conditions — balance matches expected value.
```

**Parallel Stream Simulation** — batch deposits via `IntStream.parallel()` across ForkJoinPool threads.

### Account & Customer Types

| Account Type | Details |
|---|---|
| Savings | Interest rate: 3.5%, Minimum balance: $500 |
| Checking | Overdraft limit: $1,000, Monthly fee: $10 |

| Customer Type | Details |
|---|---|
| Regular | Standard banking services |
| Premium | Enhanced benefits, minimum balance $10,000, fee waiver eligibility |

### Validation Rules

| Field | Rule |
|---|---|
| Account Number | Must match `ACC-\d{3}` (e.g. ACC-001) |
| Email | Must match `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$` |
| Name | Letters and spaces only |
| Age | 18–120 |
| Contact | Digits, +, -, (, ), spaces |

---

## Technical Highlights

| Area | Implementation |
|---|---|
| Account storage | `HashMap<String, Account>` — O(1) lookup by account number |
| Transaction storage | `Collections.synchronizedList(new ArrayList<>())` — thread-safe |
| Sorting & filtering | Java Streams with `Comparator.comparing()`, `filter()`, `mapToDouble()` |
| File I/O | `java.nio.file.Files.lines()` + `stream().map()` + `Collectors.toMap()` |
| Validation | `java.util.regex.Pattern`, `Matcher`, and `Predicate<String>` |
| Thread safety | `synchronized` methods on Account, ordered locking for transfers, `AtomicInteger` for IDs |
| Concurrency demo | Manual `Thread` + `join()`, and `IntStream.parallel()` with ForkJoinPool |
| Logging | Centralized `ConsoleLogger` with `[HH:mm:ss.SSS] [TAG]` format |

---

## File Persistence

Accounts are stored in `accounts.txt` (pipe-delimited) and automatically:
- **Loaded** on application startup
- **Saved** after account creation, transactions, and on exit

Static counters (account IDs, customer IDs) are restored from file to prevent collisions.

---

## Author

Leandre
