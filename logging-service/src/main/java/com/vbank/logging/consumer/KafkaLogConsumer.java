package com.vbank.logging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbank.logging.model.LogEntry;
import com.vbank.logging.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class KafkaLogConsumer {

    @Autowired
    private LogRepository logRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "banking-logs", groupId = "logging-service")
    public void consume(String rawMessage) {
        try {
            Map<String, String> payload = objectMapper.readValue(rawMessage, Map.class);
            LogEntry entry = new LogEntry();
            entry.setMessage(payload.get("message"));
            entry.setMessageType(payload.get("messageType"));
            entry.setDateTime(LocalDateTime.parse(payload.get("dateTime")));
            logRepository.save(entry);
        } catch (Exception e) {
            System.err.println("Failed to parse log message: " + e.getMessage());
        }
    }
}