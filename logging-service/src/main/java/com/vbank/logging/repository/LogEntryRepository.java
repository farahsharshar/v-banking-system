package com.vbank.logging.repository;

import com.vbank.logging.model.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByMessageType(String messageType);

    List<LogEntry> findByServiceName(String serviceName);

    List<LogEntry> findByAppName(String appName);

    @Query("SELECT l FROM LogEntry l WHERE l.dateTime BETWEEN :startDate AND :endDate")
    List<LogEntry> findByDateTimeBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT l FROM LogEntry l WHERE l.messageType = :messageType AND l.serviceName = :serviceName")
    List<LogEntry> findByMessageTypeAndServiceName(@Param("messageType") String messageType,
                                                   @Param("serviceName") String serviceName);

    Page<LogEntry> findByServiceNameOrderByDateTimeDesc(String serviceName, Pageable pageable);

    Page<LogEntry> findAllByOrderByDateTimeDesc(Pageable pageable);

    @Query("SELECT COUNT(l) FROM LogEntry l WHERE l.messageType = 'Request' AND l.dateTime >= :since")
    Long countRequestsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(l) FROM LogEntry l WHERE l.messageType = 'Response' AND l.dateTime >= :since")
    Long countResponsesSince(@Param("since") LocalDateTime since);
}