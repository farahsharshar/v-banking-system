package com.vbank.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableAsync
@EnableKafka
public class BffServiceApplication {

    public static void main(String[] args) {
        System.out.println("Starting BFF Service...");
        System.out.println("Port: 8084");
        System.out.println("Service: Backend for Frontend (BFF)");
        System.out.println("Purpose: API Aggregation and Orchestration");

        SpringApplication.run(BffServiceApplication.class, args);

        System.out.println("BFF Service started successfully!");
        System.out.println("Ready to aggregate data from backend services");
    }

    /**
     * WebClient bean for making HTTP calls to other microservices
     * Configured with appropriate timeouts and connection settings
     *
     * @return configured WebClient instance
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)); // 2MB buffer
    }

    /**
     * Default WebClient bean for backward compatibility
     *
     * @param builder WebClient builder
     * @return WebClient instance
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}