package com.vbank.account.service;

import com.vbank.account.dto.AccountCreationDto;
import com.vbank.account.dto.AccountResponseDto;
import com.vbank.account.dto.TransferDto;
import com.vbank.account.model.Account;
import com.vbank.account.model.Account.AccountStatus;
import com.vbank.account.repository.AccountRepository;
import com.vbank.account.exception.AccountNotFoundException;
import com.vbank.account.exception.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.UUID;
@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private final Random random = new Random();

    public AccountResponseDto createAccount(AccountCreationDto creationDto) {
        // Generate unique account number
        String accountNumber = generateAccountNumber();

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUserId(creationDto.getUserId());
        account.setAccountType(creationDto.getAccountType());
        account.setBalance(creationDto.getInitialBalance());

        Account savedAccount = accountRepository.save(account);

        return new AccountResponseDto(savedAccount.getId(), savedAccount.getAccountNumber(),
                "Account created successfully.");
    }

    public AccountResponseDto getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found."));

        return new AccountResponseDto(account.getId(), account.getAccountNumber(),
                account.getAccountType(), account.getBalance(), account.getStatus());
    }

    public List<AccountResponseDto> getAccountsByUserId(UUID userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for user ID " + userId + ".");
        }

        return accounts.stream()
                .map(account -> new AccountResponseDto(account.getId(), account.getAccountNumber(),
                        account.getAccountType(), account.getBalance(), account.getStatus()))
                .collect(Collectors.toList());
    }

    public String transferFunds(TransferDto transferDto) {
        Account fromAccount = accountRepository.findById(TransferDto.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException("From account not found."));

        Account toAccount = accountRepository.findById(TransferDto.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException("To account not found."));

        // Check sufficient funds
        if (fromAccount.getBalance().compareTo(transferDto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the from account.");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferDto.getAmount()));
        fromAccount.setLastTransactionAt(LocalDateTime.now());

        toAccount.setBalance(toAccount.getBalance().add(transferDto.getAmount()));
        toAccount.setLastTransactionAt(LocalDateTime.now());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return "Account updated successfully.";
    }

    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    public void inactivateStaleAccounts() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<Account> staleAccounts = accountRepository.findStaleAccounts(AccountStatus.ACTIVE, threshold);

        for (Account account : staleAccounts) {
            account.setStatus(AccountStatus.INACTIVE);
            accountRepository.save(account);
        }

        if (!staleAccounts.isEmpty()) {
            System.out.println("Inactivated " + staleAccounts.size() + " stale accounts");
        }
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%010d", random.nextInt(1000000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}