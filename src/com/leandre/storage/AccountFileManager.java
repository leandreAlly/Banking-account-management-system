package com.leandre.storage;

import com.leandre.account.Account;
import com.leandre.account.CheckingAccount;
import com.leandre.account.SavingAccount;
import com.leandre.customer.Customer;
import com.leandre.customer.PremiumCustomer;
import com.leandre.customer.RegularCustomer;

import com.leandre.cli.ConsoleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountFileManager {

    private static final String FILE_NAME = "accounts.txt";
    private static final String DELIMITER = "|";
    private static final String HEADER = "accountNumber|accountType|balance|status|field1|field2"
            + "|customerId|customerName|customerAge|customerContact|customerEmail|customerAddress|customerType|customerField";

    private final Path filePath;

    public AccountFileManager() {
        this.filePath = Paths.get(FILE_NAME);
    }

    public AccountFileManager(String directory) {
        this.filePath = Paths.get(directory, FILE_NAME);
    }


    public void saveAccounts(Map<String, Account> accounts) throws IOException {
        List<String> lines = Stream.concat(
                Stream.of(HEADER),
                accounts.values().stream().map(AccountFileManager::accountToLine)
        ).collect(Collectors.toList());

        ConsoleLogger.fileInfo("Writing " + accounts.size() + " account(s) to " + filePath.toAbsolutePath() + "...");
        Files.write(filePath, lines);
        ConsoleLogger.fileSaved(filePath.toString(), accounts.size());
    }

    private static String accountToLine(Account account) {
        Customer c = account.getCustomer();

        String accountType;
        String field1;
        String field2;

        if (account instanceof SavingAccount) {
            SavingAccount sa = (SavingAccount) account;
            accountType = "Savings";
            field1 = String.valueOf(sa.getInterestRate());
            field2 = String.valueOf(sa.getMinimumBalance());
        } else {
            CheckingAccount ca = (CheckingAccount) account;
            accountType = "Checking";
            field1 = String.valueOf(ca.getOverdraftLimit());
            field2 = String.valueOf(ca.getMonthlyFee());
        }

        String customerType = c.getCustomerType();
        String customerField = "";
        if (c instanceof PremiumCustomer) {
            customerField = String.valueOf(((PremiumCustomer) c).getMinimumBalance());
        }

        return String.join(DELIMITER,
                account.getAccountNumber(),
                accountType,
                String.valueOf(account.getBalance()),
                account.getStatus(),
                field1,
                field2,
                c.getCustomerId(),
                c.getName(),
                String.valueOf(c.getAge()),
                c.getContact(),
                c.getEmail(),
                c.getAddress(),
                customerType,
                customerField);
    }

    public Map<String, Account> loadAccounts() throws IOException {
        if (!Files.exists(filePath)) {
            ConsoleLogger.fileNotFound(filePath.toString());
            return new java.util.HashMap<>();
        }
        ConsoleLogger.fileInfo("Reading accounts from " + filePath.toAbsolutePath() + "...");

        Map<String, Account> accounts;

        try (Stream<String> lines = Files.lines(filePath)) {
            accounts = lines
                    .skip(1)
                    .map(AccountFileManager::lineToAccount)
                    .collect(Collectors.toMap(
                            Account::getAccountNumber,
                            account -> account
                    ));
        }
        restoreCounters(accounts);

        ConsoleLogger.fileLoaded(filePath.toString(), accounts.size());
        // Log each loaded account for visibility
        accounts.values().forEach(a ->
                ConsoleLogger.fileInfo("  Restored: " + a.getAccountNumber()
                        + " | " + a.getCustomer().getName()
                        + " | " + a.getAccountType()
                        + " | $" + String.format("%,.2f", a.getBalance())));
        return accounts;
    }

    private static Account lineToAccount(String line) {
        String[] parts = line.split("\\|", -1);

        // Parse account fields
        String accountNumber = parts[0];
        String accountType = parts[1];
        double balance = Double.parseDouble(parts[2]);
        String status = parts[3];
        double field1 = Double.parseDouble(parts[4]);
        double field2 = Double.parseDouble(parts[5]);

        // Parse customer fields
        String customerId = parts[6];
        String customerName = parts[7];
        int customerAge = Integer.parseInt(parts[8]);
        String customerContact = parts[9];
        String customerEmail = parts[10];
        String customerAddress = parts[11];
        String customerType = parts[12];
        String customerField = parts.length > 13 ? parts[13] : "";

        Customer customer;
        if (customerType.equals("Premium")) {
            double premiumMinBalance = customerField.isEmpty() ? 10_000 : Double.parseDouble(customerField);
            customer = new PremiumCustomer(customerId, customerName, customerAge, customerContact, customerEmail, customerAddress, premiumMinBalance);
        } else {
            customer = new RegularCustomer(customerId, customerName, customerAge, customerContact, customerEmail, customerAddress);
        }

        customer.setCustomerId(customerId);

        Account account;
        if (accountType.equals("Savings")) {
            account = new SavingAccount(accountNumber, balance, status, customer, field1, field2);
        } else {
            account = new CheckingAccount(accountNumber, balance, status, customer, field1, field2);
        }

        return account;
    }

    private void restoreCounters(Map<String, Account> accounts) {
        if (accounts.isEmpty()) return;

        int maxAccountNum = accounts.keySet().stream()
                .map(key -> key.replace("ACC-", ""))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        Account.setAccountCounter(maxAccountNum);

        // Find the highest customer counter from CUST### format
        int maxCustomerNum = accounts.values().stream()
                .map(a -> a.getCustomer().getCustomerId())
                .map(id -> id.replaceAll("[^0-9]", ""))
                .filter(num -> !num.isEmpty())
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        Customer.setCustomerCounter(maxCustomerNum);
    }

    public Path getFilePath() {
        return filePath;
    }
}