package com.vbank.bff.service;

import com.vbank.bff.model.TransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BffService {

    private final WebClient webClient = WebClient.create();

    public String initiateTransfer(TransactionRequest request) {
        // Call Transaction Service
        String transactionResponse = webClient.post()
                .uri("http://localhost:8083/api/transactions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return "Transfer initiated: " + transactionResponse;
    }
}