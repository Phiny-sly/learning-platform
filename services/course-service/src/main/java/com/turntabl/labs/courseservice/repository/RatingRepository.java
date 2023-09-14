package com.turntabl.labs.courseservice.repository;

import com.turntabl.labs.courseservice.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Page<Rating> findAll(Pageable pageable);

    Page<Rating> findAllByCourseId(UUID course, Pageable pageable);

}
