// LoggingException.java
package com.vbank.logging.exception;

/**
 * Base exception for logging service operations
 */
public class LoggingException extends RuntimeException {

    public LoggingException(String message) {
        super(message);
    }

    public LoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}

