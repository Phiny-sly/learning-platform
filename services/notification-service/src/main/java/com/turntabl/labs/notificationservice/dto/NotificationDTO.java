package com.turntabl.labs.notificationservice.dto;

import com.turntabl.labs.notificationservice.model.NotificationStatus;
import com.turntabl.labs.notificationservice.model.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}

