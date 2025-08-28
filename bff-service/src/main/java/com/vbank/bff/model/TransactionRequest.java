package com.vbank.bff.model;

import java.util.UUID;

public class TransactionRequest {
    private UUID fromAccountId;
    private UUID toAccountId;
    private double amount;

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public UUID getFromAccountId() {
        return fromAccountId;
    }
    public void setFromAccountId(UUID fromAccountId) {
        this.fromAccountId = fromAccountId;
    }
    public UUID getToAccountId() {
        return toAccountId;
    }
    public void setToAccountId(UUID toAccountId) {
        this.toAccountId = toAccountId;
    }

}