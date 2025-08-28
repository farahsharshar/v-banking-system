package com.vbank.bff.client;

import com.vbank.bff.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
@Service
public class TransactionServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${transaction.service.url:http://localhost:8083}")
    private String transactionServiceUrl;

    public Mono<List<TransactionDto>> getAccountTransactions(UUID accountId) {
        WebClient webClient = webClientBuilder.baseUrl(transactionServiceUrl).build();

        return webClient.get()
                .uri("/accounts/{accountId}/transactions", accountId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TransactionDto>>() {})
                .onErrorReturn(new ArrayList<>()); // Return empty list on error
    }
}