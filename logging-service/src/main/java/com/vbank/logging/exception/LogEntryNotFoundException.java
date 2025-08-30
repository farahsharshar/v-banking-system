package com.vbank.logging.exception;

/**
 * Exception thrown when a requested log entry is not found
 */
public class LogEntryNotFoundException extends LoggingException {

    public LogEntryNotFoundException(String message) {
        super(message);
    }

    public LogEntryNotFoundException(Long logEntryId) {
        super("Log entry with ID " + logEntryId + " not found");
    }
}