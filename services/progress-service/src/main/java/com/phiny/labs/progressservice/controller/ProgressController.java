package com.phiny.labs.progressservice.controller;

import com.phiny.labs.common.exception.AuthenticationException;
import com.phiny.labs.common.security.SecurityUtils;
import com.phiny.labs.progressservice.dto.CourseProgressDTO;
import com.phiny.labs.progressservice.dto.UpdateProgressDTO;
import com.phiny.labs.progressservice.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PutMapping("/course/{courseId}/student/{studentId}/lesson")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public CourseProgressDTO updateLessonProgress(
            @PathVariable UUID courseId,
            @PathVariable UUID studentId,
            @RequestBody UpdateProgressDTO dto) {
        // Security check is done in service layer
        return progressService.updateLessonProgress(courseId, studentId, dto);
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public CourseProgressDTO getCourseProgress(
            @PathVariable UUID courseId,
            @PathVariable UUID studentId) {
        // Security check is done in service layer
        return progressService.getCourseProgress(courseId, studentId);
    }
    
    @PutMapping("/course/{courseId}/lesson")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public CourseProgressDTO updateMyLessonProgress(
            @PathVariable UUID courseId,
            @RequestBody UpdateProgressDTO dto) {
        // Auto-use current user's ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthenticationException("Authentication required");
        }
        UUID currentUserUUID = new UUID(0, currentUserId);
        return progressService.updateLessonProgress(courseId, currentUserUUID, dto);
    }

    @GetMapping("/course/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public CourseProgressDTO getMyCourseProgress(@PathVariable UUID courseId) {
        // Auto-use current user's ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthenticationException("Authentication required");
        }
        UUID currentUserUUID = new UUID(0, currentUserId);
        return progressService.getCourseProgress(courseId, currentUserUUID);
    }
}

