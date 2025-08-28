package com.vbank.logging.repository;

import com.vbank.logging.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {


    List<LogEntry> findByMessageType(String messageType);


    List<LogEntry> findByDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<LogEntry> findByMessageTypeAndDateTimeBetween(String messageType,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate);


    @Query("SELECT l FROM LogEntry l WHERE l.message LIKE %:searchText%")
    List<LogEntry> findByMessageContaining(@Param("searchText") String searchText);

    @Query("SELECT l FROM LogEntry l ORDER BY l.dateTime DESC")
    List<LogEntry> findRecentLogs(@Param("limit") int limit);


    List<LogEntry> findByMessageTypeOrderByDateTimeDesc(String messageType);


    long countByMessageType(String messageType);


    long countByDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT l FROM LogEntry l WHERE l.dateTime >= :startOfDay AND l.dateTime <= :endOfDay ORDER BY l.dateTime DESC")
    List<LogEntry> findTodaysLogs(@Param("startOfDay") LocalDateTime startOfDay,
                                  @Param("endOfDay") LocalDateTime endOfDay);


    @Query("SELECT l FROM LogEntry l WHERE " +
            "LOWER(l.message) LIKE '%error%' OR " +
            "LOWER(l.message) LIKE '%exception%' OR " +
            "LOWER(l.message) LIKE '%failed%' OR " +
            "LOWER(l.message) LIKE '%\"status\":40%' OR " +
            "LOWER(l.message) LIKE '%\"status\":50%' " +
            "ORDER BY l.dateTime DESC")
    List<LogEntry> findErrorLogs();


    @Query("SELECT l FROM LogEntry l WHERE l.message LIKE %:servicePattern% ORDER BY l.dateTime DESC")
    List<LogEntry> findByServicePattern(@Param("servicePattern") String servicePattern);

    @Query("DELETE FROM LogEntry l WHERE l.dateTime < :cutoffDate")
    int deleteLogsBeforeDate(@Param("cutoffDate") LocalDateTime cutoffDate);


    @Query("SELECT l FROM LogEntry l WHERE l.message LIKE %:userId% ORDER BY l.dateTime DESC")
    List<LogEntry> findLogsByUserId(@Param("userId") String userId);

    @Query("SELECT l FROM LogEntry l WHERE l.message LIKE %:accountId% ORDER BY l.dateTime DESC")
    List<LogEntry> findLogsByAccountId(@Param("accountId") String accountId);


    @Query("SELECT l.messageType, COUNT(l) FROM LogEntry l " +
            "WHERE l.dateTime BETWEEN :startDate AND :endDate " +
            "GROUP BY l.messageType")
    List<Object[]> getLogStatsByMessageType(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
}