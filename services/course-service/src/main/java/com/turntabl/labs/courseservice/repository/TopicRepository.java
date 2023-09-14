package com.turntabl.labs.courseservice.repository;

import com.turntabl.labs.courseservice.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {

    @Query(value = "select * from topics where title ilike %:search%", nativeQuery = true)
    Page<Topic> findAll(@Param("search") String search, Pageable pageable);

    @Query(value = "select * from topics where course_id = :courseId and title ilike %:search%", nativeQuery = true)
    Page<Topic> findAllByCourseId(@Param("search") String search, @Param("courseId") UUID course, Pageable pageable);

}
