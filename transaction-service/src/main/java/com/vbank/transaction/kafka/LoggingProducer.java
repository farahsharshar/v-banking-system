package com.vbank.transaction.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class LoggingProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "banking-logs";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void logRequest(String message, String endpoint) {
        Map<String, Object> logMessage = Map.of(
                "message", message,
                "messageType", "Request",
                "endpoint", endpoint,
                "dateTime", LocalDateTime.now().format(formatter)
        );

        kafkaTemplate.send(TOPIC, logMessage);
    }

    public void logResponse(String message, String endpoint) {
        Map<String, Object> logMessage = Map.of(
                "message", message,
                "messageType", "Response",
                "endpoint", endpoint,
                "dateTime", LocalDateTime.now().format(formatter)
        );

        kafkaTemplate.send(TOPIC, logMessage);
    }
}