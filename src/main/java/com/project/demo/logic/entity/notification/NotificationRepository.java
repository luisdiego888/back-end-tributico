package com.project.demo.logic.entity.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE " +
            "LOWER(CAST(n.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(n.name AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.type) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Notification> searchNotifications(@Param("search") String search, Pageable pageable);

    Optional<Notification> findById(Long id);

    boolean existsByTypeAndCloseDate(String type, LocalDate closeDate);
}

