package com.phiny.labs.courseservice.controller;

import com.phiny.labs.courseservice.dto.enrollment.CreateEnrollmentDTO;
import com.phiny.labs.courseservice.dto.enrollment.EnrollmentDTO;
import com.phiny.labs.courseservice.dto.enrollment.UpdateEnrollmentDTO;
import com.phiny.labs.courseservice.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    @Autowired private EnrollmentService enrollmentService;

    @PostMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    EnrollmentDTO create(
            @RequestBody CreateEnrollmentDTO payload,
            @RequestParam(required = true) UUID course
    ){
        return enrollmentService.create(course, payload);
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    List<EnrollmentDTO> read(
            @RequestParam(required = false) UUID course,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){ return  enrollmentService.read(page, size, sort, course); }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    EnrollmentDTO readById(@PathVariable("id") UUID id){
        return enrollmentService.readById(id);
    }

    @PatchMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('INSTRUCTOR')")
    EnrollmentDTO updateById(@PathVariable("id") UUID id, @RequestBody UpdateEnrollmentDTO payload){
        return enrollmentService.updateById(id, payload);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    void removeById(@PathVariable("id") UUID id){
        enrollmentService.deleteById(id);
    }

}
