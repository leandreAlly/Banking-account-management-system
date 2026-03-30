package com.leandre.account;

import com.leandre.customer.RegularCustomer;
import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.OverdraftExceededException;
import com.leandre.transaction.TransactionManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    //TODO: add before each to reset test data.
    @Test
    void depositIncreasesBalance() {
        Account account = new CheckingAccount("ACC-001", 1000, "Active",
                new RegularCustomer("CUST-001", "John", 25, "0788888888", "john@test.com", "Kigali"));
        account.deposit(500);
        assertEquals(1500, account.getBalance());
    }

    @Test
    void depositNegativeAmountThrowsException() {
        Account account = new CheckingAccount("ACC-001", 1000, "Active",
                new RegularCustomer("CUST-001", "John", 25, "0788888888", "john@test.com", "Kigali"));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
    }

    @Test
    void withdrawFromCheckingAccount() throws InsufficientFundsException, OverdraftExceededException {
        Account account = new CheckingAccount("ACC-001", 1000, "Active",
                new RegularCustomer("CUST-001", "John", 25, "0788888888", "john@test.com", "Kigali"));
        account.withdraw(500);
        assertEquals(500, account.getBalance());
    }

    @Test
    void withdrawExceedingOverdraftThrowsException() {
        Account account = new CheckingAccount("ACC-001", 500, "Active",
                new RegularCustomer("CUST-001", "John", 25, "0788888888", "john@test.com", "Kigali"));
        assertThrows(OverdraftExceededException.class, () -> account.withdraw(2000));
    }

    @Test
    void withdrawFromSavingsRespectsMinimumBalance() {
        Account account = new SavingAccount("ACC-002", 1000, "Active",
                new RegularCustomer("CUST-002", "Jane", 30, "0788888889", "jane@test.com", "Kigali"));
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(600));
    }

    @Test
    void withdrawFromSavingsAllowedAboveMinimum() throws InsufficientFundsException, OverdraftExceededException {
        Account account = new SavingAccount("ACC-002", 1000, "Active",
                new RegularCustomer("CUST-002", "Jane", 30, "0788888889", "jane@test.com", "Kigali"));
        account.withdraw(400);
        assertEquals(600, account.getBalance());
    }

    @Test
    void transferFailsWhenSourceHasInsufficientFunds() {
        Account source = new CheckingAccount("ACC-001", 500, "Active",
                new RegularCustomer("CUST-001", "John", 25, "0788888888", "john@test.com", "Kigali"));
        Account dest = new CheckingAccount("ACC-002", 1000, "Active",
                new RegularCustomer("CUST-002", "Jane", 30, "0788888889", "jane@test.com", "Kigali"));
        TransactionManager transactionManager = new TransactionManager();

        assertThrows(OverdraftExceededException.class,
                () -> transactionManager.executeTransfer(source, dest, 2000));

        assertEquals(500, source.getBalance());
        assertEquals(1000, dest.getBalance());
    }

    //Test
}