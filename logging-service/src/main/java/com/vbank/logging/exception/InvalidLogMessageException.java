package com.vbank.logging.exception;

/**
 * Exception thrown when an invalid log message is received
 */
public class InvalidLogMessageException extends LoggingException {

    public InvalidLogMessageException(String message) {
        super(message);
    }

    public InvalidLogMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}