package com.vbank.logging.repository;

import com.vbank.logging.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Find log entries by message type
     */
    List<LogEntry> findByMessageType(String messageType);

    /**
     * Find log entries within a date range
     */
    List<LogEntry> findByDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent log entries (last N entries)
     */
    @Query("SELECT l FROM LogEntry l ORDER BY l.dateTime DESC")
    List<LogEntry> findRecentLogEntries();

    /**
     * Find log entries by message type within a date range
     */
    @Query("SELECT l FROM LogEntry l WHERE l.messageType = :messageType AND l.dateTime BETWEEN :startDate AND :endDate ORDER BY l.dateTime DESC")
    List<LogEntry> findByMessageTypeAndDateRange(
            @Param("messageType") String messageType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Count log entries by message type
     */
    long countByMessageType(String messageType);

    /**
     * Find log entries containing specific text in message
     */
    @Query("SELECT l FROM LogEntry l WHERE l.message LIKE %:searchText% ORDER BY l.dateTime DESC")
    List<LogEntry> findByMessageContaining(@Param("searchText") String searchText);
}