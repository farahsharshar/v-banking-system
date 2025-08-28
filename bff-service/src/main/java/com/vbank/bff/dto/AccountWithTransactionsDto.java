package com.vbank.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object for Account information with associated transactions
 * Used in BFF service to aggregate account data with transaction history
 * Primarily used in dashboard endpoint response
 */
public class AccountWithTransactionsDto {

    @JsonProperty("accountId")
    @NotNull(message = "Account ID cannot be null")
    private String accountId;

    @JsonProperty("accountNumber")
    @NotNull(message = "Account number cannot be null")
    private String accountNumber;

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

    @JsonProperty("transactions")
    @Valid
    private List<TransactionDto> transactions;

    // Default constructor
    public AccountWithTransactionsDto() {
        this.balance = BigDecimal.ZERO;
        this.status = "ACTIVE";
        this.transactions = new ArrayList<>();
    }

    // Constructor with all fields
    public AccountWithTransactionsDto(String accountId, String accountNumber,
                                      String accountType, BigDecimal balance,
                                      String status, List<TransactionDto> transactions) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.status = status != null ? status : "ACTIVE";
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    // Constructor from AccountDto (without transactions initially)
    public AccountWithTransactionsDto(AccountDto accountDto) {
        this.accountId = accountDto.getAccountId();
        this.accountNumber = accountDto.getAccountNumber();
        this.accountType = accountDto.getAccountType();
        this.balance = accountDto.getBalance();
        this.status = accountDto.getStatus();
        this.transactions = new ArrayList<>();
    }

    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
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

    public boolean hasTransactions() {
        return transactions != null && !transactions.isEmpty();
    }

    public int getTransactionCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public void addTransaction(TransactionDto transaction) {
        if (this.transactions == null) {
            this.transactions = new ArrayList<>();
        }
        this.transactions.add(transaction);
    }

    public void addTransactions(List<TransactionDto> transactionList) {
        if (this.transactions == null) {
            this.transactions = new ArrayList<>();
        }
        if (transactionList != null) {
            this.transactions.addAll(transactionList);
        }
    }

    // Method to get recent transactions (limit)
    public List<TransactionDto> getRecentTransactions(int limit) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }

        int endIndex = Math.min(limit, transactions.size());
        return transactions.subList(0, endIndex);
    }

    // Override toString for better logging
    @Override
    public String toString() {
        return "AccountWithTransactionsDto{" +
                "accountId='" + accountId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", transactionCount=" + getTransactionCount() +
                '}';
    }

    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountWithTransactionsDto that = (AccountWithTransactionsDto) o;

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (accountNumber != null ? !accountNumber.equals(that.accountNumber) : that.accountNumber != null) return false;
        if (accountType != null ? !accountType.equals(that.accountType) : that.accountType != null) return false;
        if (balance != null ? balance.compareTo(that.balance) != 0 : that.balance != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return transactions != null ? transactions.equals(that.transactions) : that.transactions == null;
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (accountType != null ? accountType.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (transactions != null ? transactions.hashCode() : 0);
        return result;
    }
}