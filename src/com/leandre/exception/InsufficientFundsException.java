package com.leandre.exception;

public class InsufficientFundsException extends Exception {
    private final double currentBalance;
    private final double withdrawAmount;

    public InsufficientFundsException(String message, double currentBalance, double withdrawAmount) {
        super(message);
        this.currentBalance = currentBalance;
        this.withdrawAmount = withdrawAmount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getWithdrawAmount() {
        return withdrawAmount;
    }

    public double getDeficit() {
        return withdrawAmount - currentBalance;
    }
}