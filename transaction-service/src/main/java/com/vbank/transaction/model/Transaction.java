package com.vbank.transaction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
/**
 * Transaction Entity class representing a financial transaction
 * Maps to the transactions table in the database
 */
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_from_account", columnList = "fromAccountId"),
        @Index(name = "idx_to_account", columnList = "toAccountId"),
        @Index(name = "idx_transaction_timestamp", columnList = "timestamp"),
        @Index(name = "idx_transaction_status", columnList = "status")
})
public class Transaction {

    @Id
    @Column(name = "transaction_id", length = 36, nullable = false)
    private UUID transactionId;

    @Column(name = "from_account_id", length = 36, nullable = false)
    @NotNull(message = "From account ID cannot be null")
    private UUID fromAccountId;

    @Column(name = "to_account_id", length = 36, nullable = false)
    @NotNull(message = "To account ID cannot be null")
    private UUID toAccountId;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction status cannot be null")
    private TransactionStatus status;

    @Column(name = "timestamp", nullable = false)
    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enum for transaction status
    public enum TransactionStatus {
        INITIATED("Initiated"),
        SUCCESS("Success"),
        FAILED("Failed");

        private final String displayName;

        TransactionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Default constructor
    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.INITIATED;
    }

    // Constructor with required fields
    public Transaction(UUID transactionId, UUID fromAccountId, UUID toAccountId,
                       BigDecimal amount, String description) {
        this();
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
    }

    // Constructor with all fields except audit fields
    public Transaction(UUID transactionId, UUID fromAccountId, UUID toAccountId,
                       BigDecimal amount, String description, TransactionStatus status,
                       LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.status = status != null ? status : TransactionStatus.INITIATED;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = TransactionStatus.INITIATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isInitiated() {
        return TransactionStatus.INITIATED.equals(this.status);
    }

    public boolean isSuccess() {
        return TransactionStatus.SUCCESS.equals(this.status);
    }

    public boolean isFailed() {
        return TransactionStatus.FAILED.equals(this.status);
    }

    public boolean isCompleted() {
        return isSuccess() || isFailed();
    }

    public void markAsSuccess() {
        this.status = TransactionStatus.SUCCESS;
    }

    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
    }

    // Check if this is a transfer between different accounts
    public boolean isTransfer() {
        return !Objects.equals(fromAccountId, toAccountId);
    }

    // Override toString for better logging
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", fromAccountId='" + fromAccountId + '\'' +
                ", toAccountId='" + toAccountId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}