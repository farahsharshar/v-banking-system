package com.vbank.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.UUID;
/**
 * Data Transfer Object for Account information
 * Used for transferring account data between services and layers
 */
public class AccountDto {

    @JsonProperty("accountId")
    private UUID accountId;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("userId")
    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @JsonProperty("accountType")
    @NotNull(message = "Account type cannot be null")
    @Pattern(regexp = "^(SAVINGS|CHECKING)$", message = "Account type must be either SAVINGS or CHECKING")
    private String accountType;

    @JsonProperty("balance")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;

    @JsonProperty("status")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
    private String status;

    // Default constructor
    public AccountDto() {
        this.balance = BigDecimal.ZERO;
        this.status = "ACTIVE";
    }

    // Constructor with all fields
    public AccountDto(UUID accountId, String accountNumber, UUID userId,
                      String accountType, BigDecimal balance, String status) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.status = status != null ? status : "ACTIVE";
    }

    // Constructor without accountId (for creation)
    public AccountDto(String accountNumber, UUID userId, String accountType,
                      BigDecimal balance, String status) {
        this(null, accountNumber, userId, accountType, balance, status);
    }

    // Getters and Setters
    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Utility methods
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    public boolean isSavingsAccount() {
        return "SAVINGS".equals(this.accountType);
    }

    public boolean isCheckingAccount() {
        return "CHECKING".equals(this.accountType);
    }

    // Override toString for better logging
    @Override
    public String toString() {
        return "AccountDto{" +
                "accountId='" + accountId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                '}';
    }

    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountDto that = (AccountDto) o;

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (accountNumber != null ? !accountNumber.equals(that.accountNumber) : that.accountNumber != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (accountType != null ? !accountType.equals(that.accountType) : that.accountType != null) return false;
        if (balance != null ? balance.compareTo(that.balance) != 0 : that.balance != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (accountType != null ? accountType.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}