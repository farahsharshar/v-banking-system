package com.vbank.logging.dto;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * DTO for log entry responses
 */
public class LogResponseDto {

    private UUID id;
    private String message;
    private String messageType;
    private LocalDateTime dateTime;
    private LocalDateTime createdAt;

    public LogResponseDto() {
    }

    public LogResponseDto(UUID id, String message, String messageType, LocalDateTime dateTime, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.messageType = messageType;
        this.dateTime = dateTime;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LogResponseDto{" +
                "id=" + id +
                ", messageType='" + messageType + '\'' +
                ", dateTime=" + dateTime +
                ", createdAt=" + createdAt +
                '}';
    }
}