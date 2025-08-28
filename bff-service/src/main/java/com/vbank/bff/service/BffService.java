package com.vbank.bff.service;

import com.vbank.bff.dto.*;
import com.vbank.bff.client.UserServiceClient;
import com.vbank.bff.client.AccountServiceClient;
import com.vbank.bff.client.TransactionServiceClient;
import com.vbank.bff.exception.DashboardException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BffService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private AccountServiceClient accountServiceClient;

    @Autowired
    private TransactionServiceClient transactionServiceClient;

    public Mono<DashboardResponseDto> getDashboard(Long userId) {
        // Step 1: Get user profile
        Mono<UserProfileDto> userProfileMono = userServiceClient.getUserProfile(userId);

        // Step 2: Get user accounts
        Mono<List<AccountDto>> accountsMono = accountServiceClient.getUserAccounts(userId);

        // Step 3: For each account, get transactions and combine
        return Mono.zip(userProfileMono, accountsMono)
                .flatMap(tuple -> {
                    UserProfileDto userProfile = tuple.getT1();
                    List<AccountDto> accounts = tuple.getT2();

                    if (userProfile.getUserId() == null) {
                        return Mono.error(new DashboardException("Failed to retrieve user profile"));
                    }

                    // Get transactions for each account asynchronously
                    Flux<AccountWithTransactionsDto> accountWithTransactionsFlux = Flux.fromIterable(accounts)
                            .flatMap(account ->
                                    transactionServiceClient.getAccountTransactions(account.getAccountId())
                                            .map(transactions -> new AccountWithTransactionsDto(
                                                    account.getAccountId(),
                                                    account.getAccountNumber(),
                                                    account.getAccountType(),
                                                    account.getBalance(),
                                                    account.getStatus(),
                                                    transactions
                                            ))
                            );

                    return accountWithTransactionsFlux
                            .collectList()
                            .map(accountsWithTransactions -> new DashboardResponseDto(
                                    userProfile.getUserId(),
                                    userProfile.getUsername(),
                                    userProfile.getEmail(),
                                    userProfile.getFirstName(),
                                    userProfile.getLastName(),
                                    accountsWithTransactions
                            ));
                })
                .onErrorMap(throwable -> new DashboardException("Failed to retrieve dashboard data due to an issue with downstream services."));
    }
}