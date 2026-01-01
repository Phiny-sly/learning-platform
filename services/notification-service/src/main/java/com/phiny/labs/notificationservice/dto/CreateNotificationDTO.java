package com.phiny.labs.notificationservice.dto;

import com.phiny.labs.notificationservice.model.NotificationType;
import lombok.Data;

@Data
public class CreateNotificationDTO {
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private String email;
    private String phoneNumber;
}

