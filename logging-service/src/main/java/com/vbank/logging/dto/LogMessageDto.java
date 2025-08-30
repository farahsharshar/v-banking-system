package com.vbank.logging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * DTO for incoming Kafka log messages
 */
public class LogMessageDto {

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotBlank(message = "Message type cannot be blank")
    @Pattern(regexp = "Request|Response", message = "Message type must be either 'Request' or 'Response'")
    private String messageType;

    @NotNull(message = "Date time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dateTime;

    public LogMessageDto() {
    }

    public LogMessageDto(String message, String messageType, LocalDateTime dateTime) {
        this.message = message;
        this.messageType = messageType;
        this.dateTime = dateTime;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "LogMessageDto{" +
                "messageType='" + messageType + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}