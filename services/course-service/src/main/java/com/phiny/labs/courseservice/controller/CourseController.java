package com.phiny.labs.courseservice.controller;

import com.phiny.labs.courseservice.dto.course.CourseDTO;
import com.phiny.labs.courseservice.dto.course.CourseListDTO;
import com.phiny.labs.courseservice.dto.course.CreateCourseDTO;
import com.phiny.labs.courseservice.dto.course.UpdateCourseDTO;
import com.phiny.labs.courseservice.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("api/courses")
@RequiredArgsConstructor
public class CourseController {

    @Autowired private CourseService courseService;

    @PostMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('ADMIN')")
    CourseDTO create(@RequestBody CreateCourseDTO payload){
        return courseService.create(payload);
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    List<CourseListDTO> read(
            @RequestParam(required = false) UUID category,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){ return  courseService.read(page, size, sort, q, category); }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    CourseDTO readById(@PathVariable("id") UUID id){
        return courseService.readById(id);
    }

    @PatchMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('ADMIN')")
    CourseDTO updateById(@PathVariable("id") UUID id, @RequestBody UpdateCourseDTO payload){
        return courseService.updateById(id, payload);
    }

    @PutMapping(value = {"{id}/tags", "/{id}/tags/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('ADMIN')")
    void addCourseTags(@PathVariable("id") UUID id, @RequestBody List<UUID> tagIds){
        courseService.addCourseTags(id, tagIds);
    }

    @DeleteMapping(value = {"{id}/tags", "/{id}/tags/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('ADMIN')")
    void removeCategoryTags(@PathVariable("id") UUID id, @RequestBody List<UUID> tagIds){
        courseService.removeCourseTags(id, tagIds);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    void removeById(@PathVariable("id") UUID id){
        courseService.deleteById(id);
    }

}
