package com.vbank.account.dto;

import com.vbank.account.model.Account;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
public class AccountCreationDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;

    // Constructors
    public AccountCreationDto() {}

    public AccountCreationDto(UUID userId, Account.AccountType accountType, BigDecimal initialBalance) {
        this.userId = userId;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Account.AccountType getAccountType() { return accountType; }
    public void setAccountType(Account.AccountType accountType) { this.accountType = accountType; }

    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }
}
