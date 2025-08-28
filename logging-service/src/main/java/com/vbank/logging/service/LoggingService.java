package com.vbank.logging.service;

import com.vbank.logging.model.LogEntry;
import com.vbank.logging.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoggingService {

    @Autowired
    private LogEntryRepository logEntryRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void processLogMessage(Map<String, Object> logMessage) {
        try {
            String message = (String) logMessage.get("message");
            String messageType = (String) logMessage.get("messageType");
            String endpoint = (String) logMessage.get("endpoint");
            String dateTimeStr = (String) logMessage.get("dateTime");

            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

            LogEntry logEntry = new LogEntry(message, messageType, endpoint, dateTime);
            logEntryRepository.save(logEntry);

            System.out.println("Processed log: " + messageType + " - " + endpoint + " at " + dateTime);

        } catch (Exception e) {
            System.err.println("Error processing log message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<LogEntry> getAllLogs() {
        return logEntryRepository.findAll();
    }

    public List<LogEntry> getLogsByType(String messageType) {
        return logEntryRepository.findByMessageTypeOrderByDateTimeDesc(messageType);
    }

    public List<LogEntry> getLogsByEndpoint(String endpoint) {
        return logEntryRepository.findByEndpointOrderByDateTimeDesc(endpoint);
    }

    public List<LogEntry> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return logEntryRepository.findByDateTimeBetweenOrderByDateTimeDesc(start, end);
    }

    public long getTotalLogCount() {
        return logEntryRepository.count();
    }
}
