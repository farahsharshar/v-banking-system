package com.vbank.bff.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    public Mono<UserProfileDto> getUserProfile(Long userId) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();

        return webClient.get()
                .uri("/users/{userId}/profile", userId)
                .retrieve()
                .bodyToMono(UserProfileDto.class)
                .onErrorReturn(new UserProfileDto()); // Return empty DTO on error
    }
}
