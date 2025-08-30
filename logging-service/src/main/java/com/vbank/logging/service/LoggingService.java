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
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    private final LogEntryRepository logEntryRepository;

    @Autowired
    public LoggingService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    /**
     * Save a log entry to the database
     */
    public LogEntry saveLogEntry(String message, String messageType, LocalDateTime dateTime) {
        try {
            LogEntry logEntry = new LogEntry(message, messageType, dateTime);
            LogEntry savedEntry = logEntryRepository.save(logEntry);

            logger.debug("Successfully saved log entry with ID: {} and type: {}",
                    savedEntry.getId(), savedEntry.getMessageType());

            return savedEntry;
        } catch (Exception e) {
            logger.error("Failed to save log entry. Message type: {}, DateTime: {}. Error: {}",
                    messageType, dateTime, e.getMessage(), e);
            throw new RuntimeException("Failed to save log entry", e);
        }
    }

    /**
     * Retrieve all log entries with pagination
     */
    @Transactional(readOnly = true)
    public Page<LogEntry> getAllLogEntries(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateTime"));
            return logEntryRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries. Page: {}, Size: {}. Error: {}",
                    page, size, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve log entries", e);
        }
    }

    /**
     * Retrieve log entries by message type
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogEntriesByType(String messageType) {
        try {
            return logEntryRepository.findByMessageType(messageType);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries by type: {}. Error: {}",
                    messageType, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve log entries by type", e);
        }
    }

    /**
     * Retrieve log entries within a date range
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogEntriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return logEntryRepository.findByDateTimeBetween(startDate, endDate);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries by date range. Start: {}, End: {}. Error: {}",
                    startDate, endDate, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve log entries by date range", e);
        }
    }

    /**
     * Retrieve log entries by message type and date range
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getLogEntriesByTypeAndDateRange(String messageType,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        try {
            return logEntryRepository.findByMessageTypeAndDateRange(messageType, startDate, endDate);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entries by type and date range. Type: {}, Start: {}, End: {}. Error: {}",
                    messageType, startDate, endDate, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve log entries by type and date range", e);
        }
    }

    /**
     * Search log entries by message content
     */
    @Transactional(readOnly = true)
    public List<LogEntry> searchLogEntries(String searchText) {
        try {
            return logEntryRepository.findByMessageContaining(searchText);
        } catch (Exception e) {
            logger.error("Failed to search log entries with text: {}. Error: {}",
                    searchText, e.getMessage(), e);
            throw new RuntimeException("Failed to search log entries", e);
        }
    }

    /**
     * Get a specific log entry by ID
     */
    @Transactional(readOnly = true)
    public Optional<LogEntry> getLogEntryById(Long id) {
        try {
            return logEntryRepository.findById(id);
        } catch (Exception e) {
            logger.error("Failed to retrieve log entry by ID: {}. Error: {}",
                    id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve log entry by ID", e);
        }
    }

    /**
     * Get count of log entries by message type
     */
    @Transactional(readOnly = true)
    public long getLogEntryCountByType(String messageType) {
        try {
            return logEntryRepository.countByMessageType(messageType);
        } catch (Exception e) {
            logger.error("Failed to count log entries by type: {}. Error: {}",
                    messageType, e.getMessage(), e);
            throw new RuntimeException("Failed to count log entries by type", e);
        }
    }

    /**
     * Get recent log entries (last 100)
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getRecentLogEntries() {
        try {
            Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "dateTime"));
            return logEntryRepository.findAll(pageable).getContent();
        } catch (Exception e) {
            logger.error("Failed to retrieve recent log entries. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve recent log entries", e);
        }
    }

    /**
     * Delete old log entries (older than specified days)
     */
    public long deleteOldLogEntries(int daysOld) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            List<LogEntry> oldEntries = logEntryRepository.findByDateTimeBetween(
                    LocalDateTime.of(2020, 1, 1, 0, 0), cutoffDate);

            if (!oldEntries.isEmpty()) {
                logEntryRepository.deleteAll(oldEntries);
                logger.info("Deleted {} old log entries older than {} days", oldEntries.size(), daysOld);
                return oldEntries.size();
            }

            return 0;
        } catch (Exception e) {
            logger.error("Failed to delete old log entries. Days old: {}. Error: {}",
                    daysOld, e.getMessage(), e);
            throw new RuntimeException("Failed to delete old log entries", e);
        }
    }
}