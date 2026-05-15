package com.project.demo.logic.entity.notification;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNotificationStatusRepository extends JpaRepository<UserNotificationStatus, Long> {
    List<UserNotificationStatus> findByUserIdAndIsReadFalseAndNotification_State(Long userId, String state);
    Optional<UserNotificationStatus> findByUserIdAndNotificationId(Long userId, Long notificationId);
    List<UserNotificationStatus> findByNotificationId(Long notificationId);
    void deleteByNotificationId(Long notificationId);
    List<UserNotificationStatus> findByUserIdAndNotification_State(Long userId, String state);
}
