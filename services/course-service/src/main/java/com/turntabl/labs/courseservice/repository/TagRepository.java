package com.turntabl.labs.courseservice.repository;

import com.turntabl.labs.courseservice.model.Course;
import com.turntabl.labs.courseservice.model.Tag;
import com.turntabl.labs.courseservice.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query(value = "select * from tags where title ilike %:search%", nativeQuery = true)
    Page<Tag> findAll(@Param("search") String search, Pageable pageable);

    @Query(value = "select * from tags t join course_tags ct on t.id=ct.tag_id where ct.course_id=:courseId and t.title ilike %:search%", nativeQuery = true)
    Page<Tag> findAllByCourseId(@Param("search") String search, @Param("courseId") UUID course, Pageable pageable);

}
