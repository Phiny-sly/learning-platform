package com.turntabl.labs.progressservice.repository;

import com.turntabl.labs.progressservice.model.AssessmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, UUID> {
    List<AssessmentAttempt> findByStudentId(UUID studentId);
    List<AssessmentAttempt> findByAssessmentId(UUID assessmentId);
    List<AssessmentAttempt> findByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);
}

