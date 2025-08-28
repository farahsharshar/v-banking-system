package com.vbank.transaction.repository;

import com.vbank.transaction.model.TransactionInitiationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public interface TransactionRepository extends JpaRepository<TransactionInitiationDto, UUID> {

    @Query("SELECT t FROM TransactionInitiationDto t WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId ORDER BY t.timestamp DESC")
    List<TransactionInitiationDto> findByAccountId(Long accountId);
}