package com.leandre.exception;

public class OverdraftExceededException extends Exception {
    private final double currentBalance;
    private final double withdrawAmount;
    private final double overdraftLimit;

    public OverdraftExceededException(String message, double currentBalance, double withdrawAmount, double overdraftLimit) {
        super(message);
        this.currentBalance = currentBalance;
        this.withdrawAmount = withdrawAmount;
        this.overdraftLimit = overdraftLimit;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getWithdrawAmount() {
        return withdrawAmount;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getAmountOverLimit() {
        return withdrawAmount - (currentBalance + overdraftLimit);
    }
}