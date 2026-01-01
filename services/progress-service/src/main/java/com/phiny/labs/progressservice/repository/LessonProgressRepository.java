package com.phiny.labs.progressservice.repository;

import com.phiny.labs.progressservice.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    Optional<LessonProgress> findByLessonIdAndStudentId(UUID lessonId, UUID studentId);
    List<LessonProgress> findByCourseIdAndStudentId(UUID courseId, UUID studentId);
}

