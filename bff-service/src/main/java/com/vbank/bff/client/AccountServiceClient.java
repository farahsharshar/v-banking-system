package com.vbank.bff.client;

import com.vbank.bff.dto.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;

@Service
public class AccountServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${account.service.url:http://localhost:8082}")
    private String accountServiceUrl;

    public Mono<List<AccountDto>> getUserAccounts(Long userId) {
        WebClient webClient = webClientBuilder.baseUrl(accountServiceUrl).build();

        return webClient.get()
                .uri("/users/{userId}/accounts", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {})
                .onErrorReturn(new ArrayList<>()); // Return empty list on error
    }
}