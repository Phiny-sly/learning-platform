package com.phiny.labs.courseservice.repository;

import com.phiny.labs.courseservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query(value = "select * from categories where title ilike %:search%", nativeQuery = true)
    Page<Category> findAll(@Param("search") String search, Pageable pageable);
}
