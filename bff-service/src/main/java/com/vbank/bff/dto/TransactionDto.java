package com.vbank.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
public class TransactionDto {
    private UUID transactionId;
    private BigDecimal amount;
    private UUID toAccountId;
    private String description;
    private LocalDateTime timestamp;

    // Constructors
    public TransactionDto() {}

    public TransactionDto(UUID transactionId, BigDecimal amount, UUID toAccountId, String description, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.toAccountId = toAccountId;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public UUID getToAccountId() { return toAccountId; }
    public void setToAccountId(UUID toAccountId) { this.toAccountId = toAccountId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
