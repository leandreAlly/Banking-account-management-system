package com.leandre.transaction;

public interface Transactable {
    boolean processTransaction(double amount, String type);
}
