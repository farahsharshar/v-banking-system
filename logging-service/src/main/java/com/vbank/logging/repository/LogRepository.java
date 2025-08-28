package com.vbank.logging.repository;

import com.vbank.logging.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByMessageTypeOrderByDateTimeDesc(String messageType);
    List<LogEntry> findByEndpointOrderByDateTimeDesc(String endpoint);
    List<LogEntry> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);
}
