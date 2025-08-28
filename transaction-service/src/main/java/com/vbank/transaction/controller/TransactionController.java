package com.vbank.transaction.controller;

import com.vbank.transaction.dto.TransactionInitiationDto;
import com.vbank.transaction.dto.TransactionExecutionDto;
import com.vbank.transaction.dto.TransactionResponseDto;
import com.vbank.transaction.service.TransactionService;
import com.vbank.transaction.kafka.LoggingProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@Validated
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LoggingProducer loggingProducer;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<TransactionResponseDto> initiateTransfer(@Valid @RequestBody TransactionInitiationDto initiationDto,
                                                                   HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest(initiationDto.toString(), "POST /transactions/transfer/initiation");

        TransactionResponseDto response = transactionService.initiateTransfer(initiationDto);

        // Log response
        loggingProducer.logResponse(response.toString(), "POST /transactions/transfer/initiation");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<TransactionResponseDto> executeTransfer(@Valid @RequestBody TransactionExecutionDto executionDto,
                                                                  HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest(executionDto.toString(), "POST /transactions/transfer/execution");

        TransactionResponseDto response = transactionService.executeTransfer(executionDto);

        // Log response
        loggingProducer.logResponse(response.toString(), "POST /transactions/transfer/execution");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getAccountTransactions(@PathVariable UUID accountId,
                                                                               HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest("GET /accounts/" + accountId + "/transactions", "GET /accounts/{accountId}/transactions");

        List<TransactionResponseDto> response = transactionService.getAccountTransactions(accountId);

        // Log response
        loggingProducer.logResponse(response.toString(), "GET /accounts/{accountId}/transactions");

        return ResponseEntity.ok(response);
    }
}