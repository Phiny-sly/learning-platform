package com.phiny.labs.contentmanagement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "course-service", path = "/api/courses")
public interface CourseServiceClient {

    @GetMapping("/{id}")
    CourseDto getCourseById(@PathVariable("id") UUID id);

    class CourseDto {
        private UUID id;
        private String title;
        private String description;

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}

