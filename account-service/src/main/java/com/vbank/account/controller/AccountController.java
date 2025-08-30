package com.vbank.account.controller;
import com.vbank.account.dto.AccountCreationDto;
import com.vbank.account.dto.AccountResponseDto;
import com.vbank.account.dto.TransferDto;
import com.vbank.account.service.AccountService;
import com.vbank.account.kafka.LoggingProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/accounts")
@Validated
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoggingProducer loggingProducer;

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountCreationDto creationDto,
                                                            HttpServletRequest request) {
        AccountResponseDto response = accountService.createAccount(creationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable UUID accountId, HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest("GET /accounts/" + accountId, "GET /accounts/{accountId}");

        AccountResponseDto response = accountService.getAccountById(accountId);

        // Log response
        loggingProducer.logResponse(response.toString(), "GET /accounts/{accountId}");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<List<AccountResponseDto>> getUserAccounts(@PathVariable UUID userId,
                                                                    HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest("GET /users/" + userId + "/accounts", "GET /users/{userId}/accounts");

        List<AccountResponseDto> response = accountService.getAccountsByUserId(userId);

        // Log response
        loggingProducer.logResponse(response.toString(), "GET /users/{userId}/accounts");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/transfer")
    public ResponseEntity<Map<String, String>> transferFunds(@Valid @RequestBody TransferDto transferDto,
                                                             HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest(transferDto.toString(), "PUT /accounts/transfer");

        String message = accountService.transferFunds(transferDto);
        Map<String, String> response = Map.of("message", message);

        // Log response
        loggingProducer.logResponse(response.toString(), "PUT /accounts/transfer");

        return ResponseEntity.ok(response);
    }
}