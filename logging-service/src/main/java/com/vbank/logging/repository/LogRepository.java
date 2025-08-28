package com.vbank.logging.repository;

import com.vbank.logging.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<LogEntry, UUID> {
}