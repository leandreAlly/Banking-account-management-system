package com.leandre.transaction;

import com.leandre.exception.InsufficientFundsException;

public interface Transactable {
    boolean processTransaction(double amount, String type) throws InsufficientFundsException;
}
