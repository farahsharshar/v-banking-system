package com.vbank.bff.dto;

import java.math.BigDecimal;
import java.util.List;

public class AccountWithTransactionsDto {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;

    // Constructors
    public AccountDto() {}

    public AccountDto(Long accountId, String accountNumber, String accountType, BigDecimal balance, String status) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}