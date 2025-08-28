package com.vbank.transaction.service;

import com.vbank.transaction.dto.TransactionExecutionDto;
import com.vbank.transaction.dto.TransactionResponseDto;
import com.vbank.transaction.dto.TransactionInitiationDto;
import com.vbank.transaction.model.Transaction;
import com.vbank.transaction.model.Transaction.TransactionStatus;
import com.vbank.transaction.repository.TransactionRepository;
import com.vbank.transaction.exception.TransactionNotFoundException;
import com.vbank.transaction.exception.InvalidTransactionException;
import com.vbank.transaction.client.AccountServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountServiceClient accountServiceClient;

    public TransactionResponseDto initiateTransfer(TransactionInitiationDto initiationDto) {
        // Validate accounts exist
        if (!accountServiceClient.accountExists(initiationDto.getFromAccountId()) ||
                !accountServiceClient.accountExists(initiationDto.getToAccountId())) {
            throw new InvalidTransactionException("Invalid 'from' or 'to' account ID.");
        }

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(initiationDto.getFromAccountId());
        transaction.setToAccountId(initiationDto.getToAccountId());
        transaction.setAmount(initiationDto.getAmount());
        transaction.setDescription(initiationDto.getDescription());
        transaction.setStatus(TransactionStatus.INITIATED);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDto(savedTransaction.getTransactionId(), TransactionStatus.INITIATED,
                savedTransaction.getTimestamp());
    }

    public TransactionResponseDto executeTransfer(TransactionExecutionDto executionDto) {
        Transaction transaction = transactionRepository.findById(executionDto.getTransactionId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));

        if (transaction.getStatus() != TransactionStatus.INITIATED) {
            throw new InvalidTransactionException("Transaction is not in INITIATED status.");
        }

        try {
            // Call Account Service to perform the actual transfer
            boolean transferResult = accountServiceClient.performTransfer(
                    transaction.getFromAccountId(),
                    transaction.getToAccountId(),
                    transaction.getAmount()
            );

            if (transferResult) {
                transaction.setStatus(TransactionStatus.SUCCESS);
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
            }

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDto(updatedTransaction.getTransactionId(), updatedTransaction.getStatus(),
                updatedTransaction.getTimestamp());
    }

    public List<TransactionResponseDto> getAccountTransactions(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for account ID " + accountId + ".");
        }

        return transactions.stream()
                .map(transaction -> {
                    // Determine the amount sign based on account perspective
                    BigDecimal displayAmount = transaction.getAmount();
                    if (transaction.getFromAccountId().equals(accountId)) {
                        displayAmount = displayAmount.negate(); // Debit
                    }

                    return new TransactionResponseDto(
                            transaction.getTransactionId(),
                            accountId,
                            transaction.getFromAccountId().equals(accountId) ?
                                    transaction.getToAccountId() : transaction.getFromAccountId(),
                            displayAmount,
                            transaction.getDescription(),
                            transaction.getTimestamp()
                    );
                })
                .collect(Collectors.toList());
    }
}
