package com.vbank.logging.controller;

import com.vbank.logging.model.LogEntry;
import com.vbank.logging.service.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LoggingController {

    private final LoggingService loggingService;

    @GetMapping
    public ResponseEntity<Page<LogEntry>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Fetching logs - page: {}, size: {}", page, size);
        Page<LogEntry> logs = loggingService.getAllLogs(page, size);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntry> getLogById(@PathVariable Long id) {
        log.info("Fetching log by id: {}", id);
        Optional<LogEntry> logEntry = loggingService.getLogById(id);

        if (logEntry.isPresent()) {
            return ResponseEntity.ok(logEntry.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/message-type/{messageType}")
    public ResponseEntity<List<LogEntry>> getLogsByMessageType(@PathVariable String messageType) {
        log.info("Fetching logs by message type: {}", messageType);
        List<LogEntry> logs = loggingService.getLogsByMessageType(messageType);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<Page<LogEntry>> getLogsByService(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Fetching logs for service: {} - page: {}, size: {}", serviceName, page, size);
        Page<LogEntry> logs = loggingService.getLogsByServiceName(serviceName, page, size);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/app/{appName}")
    public ResponseEntity<List<LogEntry>> getLogsByAppName(@PathVariable String appName) {
        log.info("Fetching logs for app: {}", appName);
        List<LogEntry> logs = loggingService.getLogsByAppName(appName);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LogEntry>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Fetching logs between {} and {}", startDate, endDate);
        List<LogEntry> logs = loggingService.getLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<LogEntry>> getLogsByMessageTypeAndService(
            @RequestParam String messageType,
            @RequestParam String serviceName) {

        log.info("Fetching logs by message type: {} and service: {}", messageType, serviceName);
        List<LogEntry> logs = loggingService.getLogsByMessageTypeAndService(messageType, serviceName);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getLogStatistics() {
        log.info("Fetching log statistics");
        Map<String, Object> statistics = loggingService.getLogStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalLogCount() {
        log.info("Fetching total log count");
        long count = loggingService.getTotalLogCount();
        return ResponseEntity.ok(Map.of("totalLogs", count));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LogEntry>> searchLogs(@RequestParam String query) {
        log.info("Searching logs with query: {}", query);
        List<LogEntry> logs = loggingService.searchLogs(query);

        // Simple filtering by message content (could be improved with database-level search)
        List<LogEntry> filteredLogs = logs.stream()
                .filter(log -> log.getMessage().toLowerCase().contains(query.toLowerCase()) ||
                        (log.getServiceName() != null && log.getServiceName().toLowerCase().contains(query.toLowerCase())) ||
                        (log.getAppName() != null && log.getAppName().toLowerCase().contains(query.toLowerCase())))
                .toList();

        return ResponseEntity.ok(filteredLogs);
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldLogs(
            @RequestParam(defaultValue = "30") int daysToKeep) {

        log.info("Cleaning up logs older than {} days", daysToKeep);
        try {
            loggingService.cleanupOldLogs(daysToKeep);
            return ResponseEntity.ok(Map.of("message", "Old logs cleaned up successfully"));
        } catch (Exception e) {
            log.error("Failed to cleanup old logs", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to cleanup old logs"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "logging-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}