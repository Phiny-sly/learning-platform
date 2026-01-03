package com.phiny.labs.notificationservice.service;

import com.phiny.labs.notificationservice.dto.CreateNotificationDTO;
import com.phiny.labs.notificationservice.dto.NotificationDTO;
import com.phiny.labs.notificationservice.model.Notification;
import com.phiny.labs.notificationservice.model.NotificationStatus;
import com.phiny.labs.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final JavaMailSender mailSender;

    @Transactional
    public NotificationDTO createNotification(CreateNotificationDTO dto) {
        Notification notification = modelMapper.map(dto, Notification.class);
        notification = notificationRepository.save(notification);
        
        // Send notification asynchronously based on type
        sendNotification(notification);
        
        return modelMapper.map(notification, NotificationDTO.class);
    }

    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(notification -> modelMapper.map(notification, NotificationDTO.class));
    }

    public NotificationDTO getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return modelMapper.map(notification, NotificationDTO.class);
    }

    @Transactional
    public void sendNotification(Notification notification) {
        try {
            switch (notification.getType()) {
                case EMAIL:
                    sendEmail(notification);
                    break;
                case SMS:
                    sendSMS(notification);
                    break;
                case PUSH:
                    sendPushNotification(notification);
                    break;
                case IN_APP:
                    // In-app notifications are already stored
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(LocalDateTime.now());
                    break;
            }
            notificationRepository.save(notification);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    private void sendEmail(Notification notification) {
        if (notification.getEmail() == null || notification.getEmail().isEmpty()) {
            throw new RuntimeException("Email address is required for email notifications");
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getEmail());
        message.setSubject(notification.getTitle());
        message.setText(notification.getMessage());
        
        mailSender.send(message);
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
    }

    private void sendSMS(Notification notification) {
        // TODO: Integrate with SMS service (Twilio, AWS SNS, etc.)
        // For now, just mark as sent
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
    }

    private void sendPushNotification(Notification notification) {
        // TODO: Integrate with push notification service (FCM, APNS, etc.)
        // For now, just mark as sent
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
    }

    @Transactional
    public void processPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository.findByStatus(NotificationStatus.PENDING);
        for (Notification notification : pendingNotifications) {
            try {
                sendNotification(notification);
            } catch (Exception e) {
                // Log error and continue with next notification
                logger.error("Failed to process notification {}: {}", notification.getId(), e.getMessage(), e);
            }
        }
    }
}

