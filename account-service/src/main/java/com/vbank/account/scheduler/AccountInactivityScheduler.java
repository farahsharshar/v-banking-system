package com.vbank.account.scheduler;

import com.vbank.account.model.Account;
import com.vbank.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AccountInactivityScheduler {

    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(fixedRate = 3600000) // every hour
    public void inactivateStaleAccounts() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            Instant lastTx = Instant.parse(account.getLastTransactionAt().toString());
            if (account.getStatus().equals(Account.AccountStatus.ACTIVE) &&
                    lastTx.isBefore(Instant.now().minus(24, ChronoUnit.HOURS))) {
                account.setStatus(Account.AccountStatus.INACTIVE);
                accountRepository.save(account);
            }
        }
    }
}