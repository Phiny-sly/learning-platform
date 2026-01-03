package com.phiny.labs.notificationservice.service;

import com.phiny.labs.common.exception.ExternalServiceException;
import com.phiny.labs.common.exception.NotificationException;
import com.phiny.labs.common.exception.ResourceNotFoundException;
import com.phiny.labs.common.exception.ServiceException;
import com.phiny.labs.common.exception.ValidationException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        return modelMapper.map(notification, NotificationDTO.class);
    }

    @Transactional
    public void sendNotification(Notification notification) {
        try {
            switch (notification.getType()) {
                case EMAIL -> sendEmail(notification);
                case SMS -> sendSMS(notification);
                case PUSH -> sendPushNotification(notification);
                case IN_APP -> {
                    // In-app notifications are already stored
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(LocalDateTime.now());
                }
            }
            notificationRepository.save(notification);
        } catch (NotificationException e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            throw e;
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            throw new NotificationException("Failed to send notification", e);
        }
    }

    private void sendEmail(Notification notification) {
        if (notification.getEmail() == null || notification.getEmail().isEmpty()) {
            throw new ValidationException("email", "Email address is required for email notifications");
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());
            
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            throw new ExternalServiceException("Email Service", "Failed to send email notification", e);
        }
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
            } catch (NotificationException | ExternalServiceException e) {
                // Log error and continue with next notification
                logger.error("Failed to process notification {}: {}", notification.getId(), e.getMessage(), e);
            } catch (Exception e) {
                // Log unexpected errors and continue
                logger.error("Unexpected error processing notification {}: {}", notification.getId(), e.getMessage(), e);
            }
        }
    }
}

