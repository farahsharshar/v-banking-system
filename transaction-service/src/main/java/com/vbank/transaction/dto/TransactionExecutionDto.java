package com.vbank.transaction.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public class TransactionExecutionDto {

    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;

    // Constructors
    public TransactionExecutionDto() {}

    public TransactionExecutionDto(UUID transactionId) {
        this.transactionId = transactionId;
    }

    // Getters and Setters
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
}
