package com.vbank.transaction.dto;

import com.vbank.transaction.model.Transaction.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
public class TransactionResponseDto {
    private UUID transactionId;
    private UUID accountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String description;
    private TransactionStatus status;
    private LocalDateTime timestamp;

    // Constructors
    public TransactionResponseDto() {}

    public TransactionResponseDto(UUID transactionId, TransactionStatus status, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public TransactionResponseDto(UUID transactionId, UUID accountId, UUID toAccountId,
                                  BigDecimal amount, String description, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public UUID getToAccountId() { return toAccountId; }
    public void setToAccountId(UUID toAccountId) { this.toAccountId = toAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
