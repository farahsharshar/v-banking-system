package com.vbank.account.repository;

import com.vbank.account.model.Account;
import com.vbank.account.model.Account.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserId(UUID userId);

    @Query("SELECT a FROM Account a WHERE a.status = :status AND a.lastTransactionAt < :threshold")
    List<Account> findStaleAccounts(AccountStatus status, LocalDateTime threshold);

    boolean existsByAccountNumber(String accountNumber);
}
