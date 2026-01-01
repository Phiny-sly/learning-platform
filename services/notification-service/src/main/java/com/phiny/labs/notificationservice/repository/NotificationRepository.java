package com.phiny.labs.notificationservice.repository;

import com.phiny.labs.notificationservice.model.Notification;
import com.phiny.labs.notificationservice.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    List<Notification> findByStatus(NotificationStatus status);
    Page<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status, Pageable pageable);
}

