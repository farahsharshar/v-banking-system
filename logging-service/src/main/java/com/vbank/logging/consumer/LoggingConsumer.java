package com.vbank.logging.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbank.logging.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class LoggingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConsumer.class);

    private final LoggingService loggingService;
    private final ObjectMapper objectMapper;

    @Value("${vbank.kafka.topic.logging:vbank-logging-topic}")
    private String loggingTopic;

    @Autowired
    public LoggingConsumer(LoggingService loggingService, ObjectMapper objectMapper) {
        this.loggingService = loggingService;
        this.objectMapper = objectMapper;
    }

    /**
     * Kafka consumer method that listens to the logging topic
     * Processes messages in the exact format specified in the architecture:
     * {
     *   "message": "<escaped JSON request or response>",
     *   "messageType": "Request" | "Response",
     *   "dateTime": "date"
     * }
     */
    @KafkaListener(topics = "#{@environment.getProperty('vbank.kafka.topic.logging', 'vbank-logging-topic')}")
    public void consumeLogMessage(@Payload String message) {

        logger.info("Received log message from Kafka topic: {}", loggingTopic);
        logger.debug("Raw message content: {}", message);

        try {
            // Parse the incoming JSON message
            JsonNode jsonNode = objectMapper.readTree(message);

            // Extract the three key values as specified in architecture
            String logMessage = extractMessage(jsonNode);
            String messageType = extractMessageType(jsonNode);
            LocalDateTime dateTime = extractDateTime(jsonNode);

            // Validate that all required fields are present
            validateLogData(logMessage, messageType, dateTime);

            // Insert the information into dump table in local database
            loggingService.saveLogEntry(logMessage, messageType, dateTime);

            logger.info("Successfully processed and stored log entry - Type: {}, DateTime: {}",
                    messageType, dateTime);

        } catch (Exception e) {
            logger.error("Failed to process Kafka log message: {}. Error: {}", message, e.getMessage(), e);
            // In production, consider sending to dead letter queue
        }
    }

    /**
     * Extract message field (escaped JSON request or response)
     */
    private String extractMessage(JsonNode jsonNode) {
        JsonNode messageNode = jsonNode.get("message");
        if (messageNode != null && !messageNode.isNull()) {
            return messageNode.asText();
        }
        return null;
    }

    /**
     * Extract messageType field (Request or Response)
     */
    private String extractMessageType(JsonNode jsonNode) {
        JsonNode messageTypeNode = jsonNode.get("messageType");
        if (messageTypeNode != null && !messageTypeNode.isNull()) {
            String type = messageTypeNode.asText();
            // Validate against allowed values as per architecture
            if ("Request".equals(type) || "Response".equals(type)) {
                return type;
            }
            logger.warn("Invalid messageType received: {}. Expected 'Request' or 'Response'", type);
        }
        return null;
    }

    /**
     * Extract dateTime field
     */
    private LocalDateTime extractDateTime(JsonNode jsonNode) {
        JsonNode dateTimeNode = jsonNode.get("dateTime");
        if (dateTimeNode != null && !dateTimeNode.isNull()) {
            String dateTimeStr = dateTimeNode.asText();
            try {
                return parseDateTime(dateTimeStr);
            } catch (DateTimeParseException e) {
                logger.warn("Failed to parse dateTime: {}. Error: {}", dateTimeStr, e.getMessage());
            }
        }
        return null;
    }

    /**
     * Parse various date time formats
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        // Support multiple formats as microservices might send different formats
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                // Continue to next pattern
            }
        }

        throw new DateTimeParseException("Unable to parse dateTime with any known format: " + dateTimeStr, dateTimeStr, 0);
    }

    /**
     * Validate that all required log data is present
     */
    private void validateLogData(String message, String messageType, LocalDateTime dateTime) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message field is required and cannot be empty");
        }

        if (messageType == null) {
            throw new IllegalArgumentException("MessageType field is required");
        }

        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime field is required");
        }
    }
}