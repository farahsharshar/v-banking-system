package com.vbank.bff.controller;

import com.vbank.bff.dto.DashboardResponseDto;
import com.vbank.bff.service.BffService;
import com.vbank.bff.kafka.LoggingProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
@RestController
@RequestMapping("/bff")
public class BffController {

    @Autowired
    private BffService bffService;

    @Autowired
    private LoggingProducer loggingProducer;

    @GetMapping("/dashboard/{userId}")
    public Mono<ResponseEntity<DashboardResponseDto>> getDashboard(@PathVariable UUID userId, HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest("GET /bff/dashboard/" + userId, "GET /bff/dashboard/{userId}");

        return bffService.getDashboard(userId)
                .map(dashboard -> {
                    // Log response
                    loggingProducer.logResponse(dashboard.toString(), "GET /bff/dashboard/{userId}");
                    return ResponseEntity.ok(dashboard);
                })
                .onErrorReturn(ResponseEntity.status(500).build());
    }
}
