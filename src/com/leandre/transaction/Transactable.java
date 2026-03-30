package com.leandre.transaction;

import com.leandre.exception.InsufficientFundsException;
import com.leandre.exception.OverdraftExceededException;

public interface Transactable {
    boolean processTransaction(double amount, String type) throws InsufficientFundsException, OverdraftExceededException;
}
