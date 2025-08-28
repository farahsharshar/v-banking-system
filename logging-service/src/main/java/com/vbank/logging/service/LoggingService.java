package com.vbank.logging.service;

import com.vbank.logging.model.LogEntry;
import com.vbank.logging.repository.LogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for centralized logging operations
 * Handles business logic for log management, processing Kafka messages,
 * and providing log analysis capabilities
 */
@Service
@Transactional
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    @Autowired
    private LogEntryRepository logEntryRepository;

    /**
     * Save a log entry from Kafka message
     * This method is called by the Kafka consumer
     *
     * @param message the log message content (JSON request/response)
     * @param messageType the type of message (Request/Response)
     * @param dateTime the timestamp of the log
     * @return saved LogEntry
     */
    public LogEntry saveLogEntry(String message, String messageType, LocalDateTime dateTime) {
        try {
            LogEntry logEntry = new LogEntry();
            logEntry.setMessage(message);
            logEntry.setMessageType(messageType);
            logEntry.setDateTime(dateTime != null ? dateTime : LocalDateTime.now());

            LogEntry savedEntry = logEntryRepository.save(logEntry);

            logger.debug("Log entry saved successfully with ID: {}", savedEntry.getId());
            return savedEntry;

        } catch (Exception e) {
            logger.error("Failed to save log entry: messageType={}, error={}",
                    messageType, e.getMessage(), e);
            throw new RuntimeException("Failed to save log entry", e);
        }
    }

    /**
     * Save a log entry with current timestamp
     *
     * @param message the log message content
     * @param messageType the type of message
     * @return saved LogEntry
     */
    public LogEntry saveLogEntry(String message, String messageType) {
        return saveLogEntry(message, messageType, LocalDateTime.now());
    }

    /**
     * Retrieve all logs with pagination
     *
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of log entries
     */
    @Transactional(readOnly = true)
    public Page<LogEntry> getAllLogs(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "dateTime"));
            return logEntryRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("Failed to retrieve logs: page={}, size={}, error={}",
                    page, size, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve logs", e);
        }
    }

    /**
     * Get logs by message type with pagination
     *
     * @param messageType the message type (Request/Response)

     * @return list of matching log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogsByMessageType(String messageType) {
        try {

            return logEntryRepository.findByMessageTypeOrderByDateTimeDesc(messageType);
        } catch (Exception e) {
            logger.error("Failed to retrieve logs by message type: messageType={}, error={}",
                    messageType, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve logs by message type", e);
        }
    }

    /**
     * Get logs within a date range
     *
     * @param startDate start date and time
     * @param endDate end date and time
     * @return list of log entries within the range
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return logEntryRepository.findByDateTimeBetween(startDate, endDate);
        } catch (Exception e) {
            logger.error("Failed to retrieve logs by date range: startDate={}, endDate={}, error={}",
                    startDate, endDate, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve logs by date range", e);
        }
    }

    /**
     * Get today's logs
     *
     * @return list of today's log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getTodaysLogs() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        return logEntryRepository.findTodaysLogs(startOfDay, endOfDay);
    }


    /**
     * Search logs containing specific text
     *
     * @param searchText text to search for
     * @return list of matching log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> searchLogs(String searchText) {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                return List.of();
            }
            return logEntryRepository.findByMessageContaining(searchText.trim());
        } catch (Exception e) {
            logger.error("Failed to search logs: searchText={}, error={}",
                    searchText, e.getMessage(), e);
            throw new RuntimeException("Failed to search logs", e);
        }
    }

    /**
     * Get error logs (logs containing error indicators)
     *
     * @return list of error log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getErrorLogs() {
        try {
            return logEntryRepository.findErrorLogs();
        } catch (Exception e) {
            logger.error("Failed to retrieve error logs: error={}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve error logs", e);
        }
    }

    /**
     * Get logs for a specific user
     *
     * @param userId the user ID to search for
     * @return list of user-related log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogsByUserId(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return List.of();
            }
            return logEntryRepository.findLogsByUserId(userId.trim());
        } catch (Exception e) {
            logger.error("Failed to retrieve logs by user ID: userId={}, error={}",
                    userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve logs by user ID", e);
        }
    }

    /**
     * Get logs for a specific account
     *
     * @param accountId the account ID to search for
     * @return list of account-related log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogsByAccountId(String accountId) {
        try {
            if (accountId == null || accountId.trim().isEmpty()) {
                return List.of();
            }
            return logEntryRepository.findLogsByAccountId(accountId.trim());
        } catch (Exception e) {
            logger.error("Failed to retrieve logs by account ID: accountId={}, error={}",
                    accountId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve logs by account ID", e);
        }
    }

    /**
     * Get logs for a specific service/endpoint
     *
     * @param servicePattern pattern to match (e.g., "/users", "/accounts")
     * @return list of service-related log entries
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogsByService(String servicePattern) {
        try {
            if (servicePattern == null || servicePattern.trim().isEmpty()) {
                return List.of();
            }
            return logEntryRepository.findByServicePattern(servicePattern.trim());
        } catch (Exception e) {
            logger.error("Failed to retrieve logs by service pattern: pattern={}, error={}",
                    servicePattern, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve logs by service pattern", e);
        }
    }

    /**
     * Get log statistics for monitoring
     *
     * @return map containing various log statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLogStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Total log count
            long totalLogs = logEntryRepository.count();
            stats.put("totalLogs", totalLogs);

            // Request/Response counts
            long requestCount = logEntryRepository.countByMessageType("Request");
            long responseCount = logEntryRepository.countByMessageType("Response");
            stats.put("requestCount", requestCount);
            stats.put("responseCount", responseCount);

            // Today's log count
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
            long todayCount = logEntryRepository.countByDateTimeBetween(startOfDay, endOfDay);
            stats.put("todayCount", todayCount);

            // Error log count (approximate - would need more complex query for exact count)
            List<LogEntry> errorLogs = logEntryRepository.findErrorLogs();
            stats.put("errorCount", errorLogs.size());

            logger.info("Generated log statistics: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("Failed to generate log statistics: error={}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate log statistics", e);
        }
    }

    /**
     * Clean up old logs (log retention)
     *
     * @param cutoffDate logs before this date will be deleted
     * @return number of deleted log entries
     */
    @Transactional
    public int cleanupOldLogs(LocalDateTime cutoffDate) {
        try {
            int deletedCount = logEntryRepository.deleteLogsBeforeDate(cutoffDate);
            logger.info("Cleaned up {} old log entries before date: {}", deletedCount, cutoffDate);
            return deletedCount;
        } catch (Exception e) {
            logger.error("Failed to cleanup old logs: cutoffDate={}, error={}",
                    cutoffDate, e.getMessage(), e);
            throw new RuntimeException("Failed to cleanup old logs", e);
        }
    }

    /**
     * Clean up logs older than specified days
     *
     * @param daysToKeep number of days to keep logs
     * @return number of deleted log entries
     */
    public int cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        return cleanupOldLogs(cutoffDate);
    }

    /**
     * Validate message type
     *
     * @param messageType the message type to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidMessageType(String messageType) {
        return "Request".equals(messageType) || "Response".equals(messageType);
    }
}