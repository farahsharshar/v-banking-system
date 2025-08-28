package com.vbank.transaction.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
@Service
public class AccountServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${account.service.url:http://localhost:8082}")
    private String accountServiceUrl;

    public boolean accountExists(UUID accountId) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(accountServiceUrl).build();

            String response = webClient.get()
                    .uri("/accounts/{accountId}", accountId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean performTransfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(accountServiceUrl).build();

            Map<String, Object> transferRequest = Map.of(
                    "fromAccountId", fromAccountId,
                    "toAccountId", toAccountId,
                    "amount", amount
            );

            String response = webClient.put()
                    .uri("/accounts/transfer")
                    .body(BodyInserters.fromValue(transferRequest))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null && response.contains("successfully");
        } catch (Exception e) {
            return false;
        }
    }
}