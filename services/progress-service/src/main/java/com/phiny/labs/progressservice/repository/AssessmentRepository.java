package com.phiny.labs.progressservice.repository;

import com.phiny.labs.progressservice.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    List<Assessment> findByCourseId(UUID courseId);
}

