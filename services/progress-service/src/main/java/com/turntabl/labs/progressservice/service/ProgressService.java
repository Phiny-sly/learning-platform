package com.turntabl.labs.progressservice.service;

import com.turntabl.labs.progressservice.dto.CourseProgressDTO;
import com.turntabl.labs.progressservice.dto.UpdateProgressDTO;
import com.turntabl.labs.progressservice.model.CourseProgress;
import com.turntabl.labs.progressservice.model.LessonProgress;
import com.turntabl.labs.progressservice.model.LessonStatus;
import com.turntabl.labs.progressservice.model.ProgressStatus;
import com.turntabl.labs.progressservice.repository.CourseProgressRepository;
import com.turntabl.labs.progressservice.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    @Transactional
    public CourseProgressDTO updateLessonProgress(UUID courseId, UUID studentId, UpdateProgressDTO dto) {
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
            }
        }

        courseProgress = courseProgressRepository.save(courseProgress);
        return modelMapper.map(courseProgress, CourseProgressDTO.class);
    }

    public CourseProgressDTO getCourseProgress(UUID courseId, UUID studentId) {
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

