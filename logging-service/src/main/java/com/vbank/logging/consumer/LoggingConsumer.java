package com.vbank.logging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbank.logging.model.LogEntry;
import com.vbank.logging.service.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingConsumer {

    private final LoggingService loggingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.logging-topic:vbank-logging}",
            groupId = "${spring.kafka.consumer.group-id:logging-service}")
    public void consume(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                        @Header(value = KafkaHeaders.OFFSET, required = false) Long offset,
                        Acknowledgment acknowledgment) {

        log.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);

        try {
            // Parse the Kafka message
            JsonNode messageNode = objectMapper.readTree(message);

            // Extract fields from the message
            String logMessage = messageNode.get("message").asText();
            String messageType = messageNode.get("messageType").asText();
            String dateTimeStr = messageNode.get("dateTime").asText();

            // Optional fields
            String serviceName = messageNode.has("serviceName") ?
                    messageNode.get("serviceName").asText() : extractServiceFromMessage(logMessage);
            String appName = messageNode.has("appName") ?
                    messageNode.get("appName").asText() : null;

            // Parse dateTime
            LocalDateTime dateTime = parseDateTime(dateTimeStr);

            // Create LogEntry
            LogEntry logEntry = LogEntry.builder()
                    .message(logMessage)
                    .messageType(messageType)
                    .dateTime(dateTime)
                    .serviceName(serviceName)
                    .appName(appName)
                    .build();

            // Save to database
            loggingService.saveLogEntry(logEntry);

            log.debug("Successfully processed log message: type={}, service={}, dateTime={}",
                    messageType, serviceName, dateTime);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message: {}", message, e);
            // Still acknowledge to avoid infinite retry
        } catch (Exception e) {
            log.error("Error processing log message: {}", message, e);
            // Still acknowledge to avoid infinite retry
        } finally {
            // Acknowledge the message
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // Try ISO format first (2025-07-15T07:16:49.822Z)
            if (dateTimeStr.contains("T")) {
                return LocalDateTime.parse(dateTimeStr.replace("Z", ""),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            // Try other common formats
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse dateTime: {}, using current time", dateTimeStr);
            return LocalDateTime.now();
        }
    }

    private String extractServiceFromMessage(String message) {
        try {
            // Try to extract service name from the message content
            JsonNode messageNode = objectMapper.readTree(message);

            // Look for common patterns in URLs or headers
            if (messageNode.has("url")) {
                String url = messageNode.get("url").asText();
                if (url.contains("users")) return "user-service";
                if (url.contains("accounts")) return "account-service";
                if (url.contains("transactions")) return "transaction-service";
                if (url.contains("bff")) return "bff-service";
            }

            // Check for service indicators in the message
            String messageContent = message.toLowerCase();
            if (messageContent.contains("user")) return "user-service";
            if (messageContent.contains("account")) return "account-service";
            if (messageContent.contains("transaction")) return "transaction-service";
            if (messageContent.contains("bff") || messageContent.contains("dashboard")) return "bff-service";

        } catch (Exception e) {
            log.debug("Could not extract service name from message", e);
        }

        return "unknown-service";
    }
}