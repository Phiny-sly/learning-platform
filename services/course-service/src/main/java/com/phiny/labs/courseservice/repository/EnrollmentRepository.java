package com.phiny.labs.courseservice.repository;

import com.phiny.labs.courseservice.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Page<Enrollment> findAll(Pageable pageable);

    Page<Enrollment> findAllByCourseId(UUID course, Pageable pageable);

}
