package com.vbank.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    private Long transactionId;
    private BigDecimal amount;
    private Long toAccountId;
    private String description;
    private LocalDateTime timestamp;

    // Constructors
    public TransactionDto() {}

    public TransactionDto(Long transactionId, BigDecimal amount, Long toAccountId, String description, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.toAccountId = toAccountId;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
