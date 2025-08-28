package com.vbank.logging.controller;

import com.vbank.logging.model.LogEntry;
import com.vbank.logging.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LoggingController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping
    public ResponseEntity<List<LogEntry>> getAllLogs() {
        List<LogEntry> logs = loggingService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/type/{messageType}")
    public ResponseEntity<List<LogEntry>> getLogsByType(@PathVariable String messageType) {
        List<LogEntry> logs = loggingService.getLogsByType(messageType);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/endpoint")
    public ResponseEntity<List<LogEntry>> getLogsByEndpoint(@RequestParam String endpoint) {
        List<LogEntry> logs = loggingService.getLogsByEndpoint(endpoint);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/daterange")
    public ResponseEntity<List<LogEntry>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<LogEntry> logs = loggingService.getLogsByDateRange(start, end);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalLogCount() {
        long count = loggingService.getTotalLogCount();
        return ResponseEntity.ok(Map.of("totalLogs", count));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "logging-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
