package com.vbank.logging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "log_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "app_name")
    private String appName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}