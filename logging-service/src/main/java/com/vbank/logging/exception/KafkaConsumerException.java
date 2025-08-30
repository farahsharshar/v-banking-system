package com.vbank.logging.exception;

/**
 * Exception thrown when Kafka consumer encounters errors
 */
public class KafkaConsumerException extends LoggingException {

    public KafkaConsumerException(String message) {
        super(message);
    }

    public KafkaConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}