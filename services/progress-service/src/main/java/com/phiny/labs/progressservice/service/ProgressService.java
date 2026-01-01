package com.phiny.labs.progressservice.service;

import com.phiny.labs.common.security.SecurityUtils;
import com.phiny.labs.progressservice.dto.CourseProgressDTO;
import com.phiny.labs.progressservice.dto.UpdateProgressDTO;
import com.phiny.labs.progressservice.exception.AccessDeniedException;
import com.phiny.labs.progressservice.model.CourseProgress;
import com.phiny.labs.progressservice.model.LessonProgress;
import com.phiny.labs.progressservice.model.LessonStatus;
import com.phiny.labs.progressservice.model.ProgressStatus;
import com.phiny.labs.progressservice.client.CourseServiceClient;
import com.phiny.labs.progressservice.client.NotificationServiceClient;
import com.phiny.labs.progressservice.repository.CourseProgressRepository;
import com.phiny.labs.progressservice.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final CourseProgressRepository courseProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final ModelMapper modelMapper;
    
    @Autowired(required = false)
    private CourseServiceClient courseServiceClient;
    
    @Autowired(required = false)
    private NotificationServiceClient notificationServiceClient;

    @Transactional
    public CourseProgressDTO updateLessonProgress(UUID courseId, UUID studentId, UpdateProgressDTO dto) {
        // Security check: Ensure user can only update their own progress (unless admin/instructor)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        
        UUID currentUserUUID = new UUID(0, currentUserId);
        if (!SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR")) {
            if (!studentId.equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only update your own progress");
            }
        }
        
        // Update or create lesson progress
        LessonProgress lessonProgress = lessonProgressRepository
                .findByLessonIdAndStudentId(dto.getLessonId(), studentId)
                .orElse(new LessonProgress());

        if (lessonProgress.getId() == null) {
            lessonProgress.setLessonId(dto.getLessonId());
            lessonProgress.setCourseId(courseId);
            lessonProgress.setStudentId(studentId);
            lessonProgress.setStatus(LessonStatus.IN_PROGRESS);
        }

        if (dto.getTimeSpentMinutes() != null) {
            lessonProgress.setTimeSpentMinutes(
                    lessonProgress.getTimeSpentMinutes() + dto.getTimeSpentMinutes()
            );
        }

        if (Boolean.TRUE.equals(dto.getCompleted())) {
            lessonProgress.setStatus(LessonStatus.COMPLETED);
            lessonProgress.setCompletedAt(LocalDateTime.now());
        } else if (lessonProgress.getStatus() == LessonStatus.NOT_STARTED) {
            lessonProgress.setStatus(LessonStatus.IN_PROGRESS);
        }

        lessonProgressRepository.save(lessonProgress);

        // Update course progress
        return updateCourseProgress(courseId, studentId);
    }

    @Transactional
    public CourseProgressDTO updateCourseProgress(UUID courseId, UUID studentId) {
        // Validate course exists if courseServiceClient is available
        if (courseServiceClient != null) {
            try {
                CourseServiceClient.CourseDto course = courseServiceClient.getCourseById(courseId);
                if (course == null) {
                    throw new RuntimeException("Course not found with id: " + courseId);
                }
            } catch (Exception e) {
                throw new RuntimeException("Course validation failed: " + e.getMessage());
            }
        }
        
        CourseProgress courseProgress = courseProgressRepository
                .findByCourseIdAndStudentId(courseId, studentId)
                .orElse(new CourseProgress());

        if (courseProgress.getId() == null) {
            courseProgress.setCourseId(courseId);
            courseProgress.setStudentId(studentId);
            courseProgress.setStatus(ProgressStatus.IN_PROGRESS);
        }

        // Get all lesson progress for this course and student
        List<LessonProgress> lessonProgresses = lessonProgressRepository
                .findByCourseIdAndStudentId(courseId, studentId);

        int totalLessons = lessonProgresses.size();
        int completedLessons = (int) lessonProgresses.stream()
                .filter(lp -> lp.getStatus() == LessonStatus.COMPLETED)
                .count();

        courseProgress.setTotalLessons(totalLessons);
        courseProgress.setCompletedLessons(completedLessons);
        courseProgress.setProgressPercentage(
                totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0.0
        );
        courseProgress.setLastAccessedAt(LocalDateTime.now());

        if (completedLessons == totalLessons && totalLessons > 0) {
            courseProgress.setStatus(ProgressStatus.COMPLETED);
            if (courseProgress.getCompletedAt() == null) {
                courseProgress.setCompletedAt(LocalDateTime.now());
                
                // Send completion notification
                if (notificationServiceClient != null) {
                    try {
                        NotificationServiceClient.CreateNotificationRequest notification = new NotificationServiceClient.CreateNotificationRequest();
                        notification.setTitle("Course Completed");
                        notification.setMessage("Congratulations! You have completed the course.");
                        notification.setType("IN_APP");
                        notificationServiceClient.createNotification(notification);
                    } catch (Exception e) {
                        System.err.println("Failed to send completion notification: " + e.getMessage());
                    }
                }
            }
        }

        courseProgress = courseProgressRepository.save(courseProgress);
        return modelMapper.map(courseProgress, CourseProgressDTO.class);
    }

    public CourseProgressDTO getCourseProgress(UUID courseId, UUID studentId) {
        // Security check: Ensure user can only view their own progress (unless admin/instructor)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        
        UUID currentUserUUID = new UUID(0, currentUserId);
        if (!SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR")) {
            if (!studentId.equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only view your own progress");
            }
        }
        
        CourseProgress courseProgress = courseProgressRepository
                .findByCourseIdAndStudentId(courseId, studentId)
                .orElse(null);

        if (courseProgress == null) {
            CourseProgressDTO dto = new CourseProgressDTO();
            dto.setCourseId(courseId);
            dto.setStudentId(studentId);
            dto.setProgressPercentage(0.0);
            dto.setCompletedLessons(0);
            dto.setTotalLessons(0);
            dto.setStatus(ProgressStatus.NOT_STARTED);
            return dto;
        }

        return modelMapper.map(courseProgress, CourseProgressDTO.class);
    }
}

