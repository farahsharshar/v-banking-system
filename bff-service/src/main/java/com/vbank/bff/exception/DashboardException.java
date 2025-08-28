package com.vbank.bff.exception;

/**
 * Custom exception class for dashboard-related errors in BFF service
 * This exception is thrown when there are issues aggregating data
 * from multiple downstream services for the dashboard endpoint
 */
public class DashboardException extends RuntimeException {

    private final String errorCode;
    private final String service;

    /**
     * Default constructor
     */
    public DashboardException() {
        super("Dashboard data aggregation failed");
        this.errorCode = "DASHBOARD_ERROR";
        this.service = "UNKNOWN";
    }

    /**
     * Constructor with message
     * @param message the error message
     */
    public DashboardException(String message) {
        super(message);
        this.errorCode = "DASHBOARD_ERROR";
        this.service = "UNKNOWN";
    }

    /**
     * Constructor with message and cause
     * @param message the error message
     * @param cause the root cause of the exception
     */
    public DashboardException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DASHBOARD_ERROR";
        this.service = "UNKNOWN";
    }

    /**
     * Constructor with message and error code
     * @param message the error message
     * @param errorCode specific error code for the dashboard error
     */
    public DashboardException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : "DASHBOARD_ERROR";
        this.service = "UNKNOWN";
    }

    /**
     * Constructor with message, error code and service name
     * @param message the error message
     * @param errorCode specific error code for the dashboard error
     * @param service the downstream service that caused the error
     */
    public DashboardException(String message, String errorCode, String service) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : "DASHBOARD_ERROR";
        this.service = service != null ? service : "UNKNOWN";
    }

    /**
     * Constructor with message, cause, error code and service name
     * @param message the error message
     * @param cause the root cause of the exception
     * @param errorCode specific error code for the dashboard error
     * @param service the downstream service that caused the error
     */
    public DashboardException(String message, Throwable cause, String errorCode, String service) {
        super(message, cause);
        this.errorCode = errorCode != null ? errorCode : "DASHBOARD_ERROR";
        this.service = service != null ? service : "UNKNOWN";
    }

    /**
     * Get the error code
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get the service that caused the error
     * @return the service name
     */
    public String getService() {
        return service;
    }

    /**
     * Static factory method for user service errors
     * @param message the error message
     * @param cause the root cause
     * @return DashboardException instance
     */
    public static DashboardException userServiceError(String message, Throwable cause) {
        return new DashboardException(message, cause, "USER_SERVICE_ERROR", "USER_SERVICE");
    }

    /**
     * Static factory method for account service errors
     * @param message the error message
     * @param cause the root cause
     * @return DashboardException instance
     */
    public static DashboardException accountServiceError(String message, Throwable cause) {
        return new DashboardException(message, cause, "ACCOUNT_SERVICE_ERROR", "ACCOUNT_SERVICE");
    }

    /**
     * Static factory method for transaction service errors
     * @param message the error message
     * @param cause the root cause
     * @return DashboardException instance
     */
    public static DashboardException transactionServiceError(String message, Throwable cause) {
        return new DashboardException(message, cause, "TRANSACTION_SERVICE_ERROR", "TRANSACTION_SERVICE");
    }

    /**
     * Static factory method for data aggregation errors
     * @param message the error message
     * @return DashboardException instance
     */
    public static DashboardException dataAggregationError(String message) {
        return new DashboardException(message, "DATA_AGGREGATION_ERROR", "BFF_SERVICE");
    }

    /**
     * Static factory method for timeout errors
     * @param service the service that timed out
     * @param cause the timeout cause
     * @return DashboardException instance
     */
    public static DashboardException timeoutError(String service, Throwable cause) {
        String message = String.format("Timeout occurred while calling %s", service);
        return new DashboardException(message, cause, "SERVICE_TIMEOUT", service);
    }

    /**
     * Static factory method for partial data errors
     * @param message the error message
     * @param failedServices list of services that failed
     * @return DashboardException instance
     */
    public static DashboardException partialDataError(String message, String failedServices) {
        return new DashboardException(message, "PARTIAL_DATA_ERROR", failedServices);
    }

    /**
     * Override toString to provide detailed error information
     * @return formatted string with error details
     */
    @Override
    public String toString() {
        return String.format("DashboardException{errorCode='%s', service='%s', message='%s'}",
                errorCode, service, getMessage());
    }
}