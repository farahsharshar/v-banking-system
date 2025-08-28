package com.vbank.logging.service;

import com.vbank.logging.model.LogEntry;
import com.vbank.logging.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {

    private final LogEntryRepository logEntryRepository;

    @Transactional
    public LogEntry saveLogEntry(LogEntry logEntry) {
        try {
            LogEntry savedEntry = logEntryRepository.save(logEntry);
            log.debug("Saved log entry with ID: {}", savedEntry.getId());
            return savedEntry;
        } catch (Exception e) {
            log.error("Failed to save log entry: {}", logEntry, e);
            throw e;
        }
    }

    public Page<LogEntry> getAllLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        return logEntryRepository.findAllByOrderByDateTimeDesc(pageable);
    }

    public List<LogEntry> getLogsByMessageType(String messageType) {
        return logEntryRepository.findByMessageType(messageType);
    }

    public List<LogEntry> getLogsByServiceName(String serviceName) {
        return logEntryRepository.findByServiceName(serviceName);
    }

    public List<LogEntry> getLogsByAppName(String appName) {
        return logEntryRepository.findByAppName(appName);
    }

    public List<LogEntry> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return logEntryRepository.findByDateTimeBetween(startDate, endDate);
    }

    public List<LogEntry> getLogsByMessageTypeAndService(String messageType, String serviceName) {
        return logEntryRepository.findByMessageTypeAndServiceName(messageType, serviceName);
    }

    public Page<LogEntry> getLogsByServiceName(String serviceName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        return logEntryRepository.findByServiceNameOrderByDateTimeDesc(serviceName, pageable);
    }

    public Optional<LogEntry> getLogById(long id) {
        return logEntryRepository.findById(id);
    }

    public long getTotalLogCount() {
        return logEntryRepository.count();
    }

    public Map<String, Object> getLogStatistics() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHour = now.minusHours(1);
        LocalDateTime lastDay = now.minusDays(1);

        // Total counts
        stats.put("totalLogs", logEntryRepository.count());
        stats.put("requestsLastHour", logEntryRepository.countRequestsSince(lastHour));
        stats.put("responsesLastHour", logEntryRepository.countResponsesSince(lastHour));
        stats.put("requestsLastDay", logEntryRepository.countRequestsSince(lastDay));
        stats.put("responsesLastDay", logEntryRepository.countResponsesSince(lastDay));

        // Service breakdown
        Map<String, Long> serviceStats = new HashMap<>();
        List<String> services = List.of("user-service", "account-service", "transaction-service", "bff-service");
        for (String service : services) {
            serviceStats.put(service, (long) logEntryRepository.findByServiceName(service).size());
        }
        stats.put("serviceBreakdown", serviceStats);

        return stats;
    }

    public void cleanupOldLogs(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            List<LogEntry> oldLogs = logEntryRepository.findByDateTimeBetween(
                    LocalDateTime.MIN, cutoffDate);

            if (!oldLogs.isEmpty()) {
                logEntryRepository.deleteAll(oldLogs);
                log.info("Cleaned up {} old log entries older than {} days",
                        oldLogs.size(), daysToKeep);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup old logs", e);
        }
    }

    @Transactional(readOnly = true)
    public List<LogEntry> searchLogs(String searchTerm) {
        // This would require a custom query with LIKE operations
        // For now, return all logs and let the controller filter
        return logEntryRepository.findAll();
    }
}