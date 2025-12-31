package com.turntabl.labs.progressservice.controller;

import com.turntabl.labs.progressservice.dto.CourseProgressDTO;
import com.turntabl.labs.progressservice.dto.UpdateProgressDTO;
import com.turntabl.labs.progressservice.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PutMapping("/course/{courseId}/student/{studentId}/lesson")
    @ResponseStatus(HttpStatus.OK)
    public CourseProgressDTO updateLessonProgress(
            @PathVariable UUID courseId,
            @PathVariable UUID studentId,
            @RequestBody UpdateProgressDTO dto) {
        return progressService.updateLessonProgress(courseId, studentId, dto);
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public CourseProgressDTO getCourseProgress(
            @PathVariable UUID courseId,
            @PathVariable UUID studentId) {
        return progressService.getCourseProgress(courseId, studentId);
    }
}

