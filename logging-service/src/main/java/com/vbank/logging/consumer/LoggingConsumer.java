package com.vbank.logging.consumer;

import com.vbank.logging.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoggingConsumer {

    @Autowired
    private LoggingService loggingService;

    @KafkaListener(topics = "banking-logs", groupId = "logging-service-group")
    public void consumeLogMessage(Map<String, Object> logMessage) {
        System.out.println("Received log message: " + logMessage);
        loggingService.processLogMessage(logMessage);
    }
}
