package com.vbank.bff.dto;

import java.util.List;
import java.util.UUID;
public class DashboardResponseDto {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<AccountWithTransactionsDto> accounts;

    // Constructors
    public DashboardResponseDto() {}

    public DashboardResponseDto(UUID userId, String username, String email, String firstName, String lastName,
                                List<AccountWithTransactionsDto> accounts) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = accounts;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<AccountWithTransactionsDto> getAccounts() { return accounts; }
    public void setAccounts(List<AccountWithTransactionsDto> accounts) { this.accounts = accounts; }
}
