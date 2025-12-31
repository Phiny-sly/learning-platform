package com.turntabl.labs.notificationservice.controller;

import com.turntabl.labs.notificationservice.dto.CreateNotificationDTO;
import com.turntabl.labs.notificationservice.dto.NotificationDTO;
import com.turntabl.labs.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDTO createNotification(@RequestBody CreateNotificationDTO dto) {
        return notificationService.createNotification(dto);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<NotificationDTO> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Sort sortObj = Sort.by(sort[0].split(",")[0]);
        if (sort[0].split(",").length > 1 && sort[0].split(",")[1].equalsIgnoreCase("desc")) {
            sortObj = sortObj.descending();
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return notificationService.getUserNotifications(userId, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDTO getNotification(@PathVariable UUID id) {
        return notificationService.getNotificationById(id);
    }

    @PostMapping("/process-pending")
    @ResponseStatus(HttpStatus.OK)
    public void processPendingNotifications() {
        notificationService.processPendingNotifications();
    }
}

