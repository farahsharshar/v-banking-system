package com.vbank.logging.controller;

import com.vbank.logging.model.LogEntry;
import com.vbank.logging.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LoggingController {

    private static final Logger logger = LoggerFactory.getLogger(LoggingController.class);

    private final LoggingService loggingService;

    @Autowired
    public LoggingController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "logging-service");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Get all log entries with pagination
     */
    @GetMapping
    public ResponseEntity<Page<LogEntry>> getAllLogEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<LogEntry> logEntries = loggingService.getAllLogEntries(page, size);
            return ResponseEntity.ok(logEntries);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get log entries by message type
     */
    @GetMapping("/type/{messageType}")
    public ResponseEntity<List<LogEntry>> getLogEntriesByType(@PathVariable String messageType) {
        try {
            if (!"Request".equals(messageType) && !"Response".equals(messageType)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid message type. Must be 'Request' or 'Response'");
                return ResponseEntity.badRequest().build();
            }

            List<LogEntry> logEntries = loggingService.getLogEntriesByType(messageType);
            return ResponseEntity.ok(logEntries);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries by type: {}. Error: {}", messageType, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get log entries by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<LogEntry>> getLogEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            List<LogEntry> logEntries = loggingService.getLogEntriesByDateRange(startDate, endDate);
            return ResponseEntity.ok(logEntries);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries by date range. Start: {}, End: {}. Error: {}",
                    startDate, endDate, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get log entries by message type and date range
     */
    @GetMapping("/type/{messageType}/date-range")
    public ResponseEntity<List<LogEntry>> getLogEntriesByTypeAndDateRange(
            @PathVariable String messageType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            if (!"Request".equals(messageType) && !"Response".equals(messageType)) {
                return ResponseEntity.badRequest().build();
            }

            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }

            List<LogEntry> logEntries = loggingService.getLogEntriesByTypeAndDateRange(messageType, startDate, endDate);
            return ResponseEntity.ok(logEntries);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries by type and date range. Type: {}, Start: {}, End: {}. Error: {}",
                    messageType, startDate, endDate, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search log entries by message content
     */
    @GetMapping("/search")
    public ResponseEntity<List<LogEntry>> searchLogEntries(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<LogEntry> logEntries = loggingService.searchLogEntries(query.trim());
            return ResponseEntity.ok(logEntries);
        } catch (Exception e) {
            logger.error("Failed to search log entries with query: {}. Error: {}", query, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific log entry by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LogEntry> getLogEntryById(@PathVariable Long id) {
        try {
            Optional<LogEntry> logEntry = loggingService.getLogEntryById(id);

            if (logEntry.isPresent()) {
                return ResponseEntity.ok(logEntry.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve log entry by ID: {}. Error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get recent log entries (last 100)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<LogEntry>> getRecentLogEntries() {
        try {
            List<LogEntry> logEntries = loggingService.getRecentLogEntries();
            return ResponseEntity.ok(logEntries);
        } catch (Exception e) {
            logger.error("Failed to retrieve recent log entries. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get log entry statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLogEntryStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            long requestCount = loggingService.getLogEntryCountByType("Request");
            long responseCount = loggingService.getLogEntryCountByType("Response");
            long totalCount = requestCount + responseCount;

            stats.put("totalEntries", totalCount);
            stats.put("requestEntries", requestCount);
            stats.put("responseEntries", responseCount);
            stats.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entry statistics. Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete old log entries
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> deleteOldLogEntries(
            @RequestParam(defaultValue = "30") int daysOld) {

        try {
            long deletedCount = loggingService.deleteOldLogEntries(daysOld);

            Map<String, Object> response = new HashMap<>();
            response.put("deletedCount", deletedCount);
            response.put("daysOld", daysOld);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete old log entries. Days old: {}. Error: {}", daysOld, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}