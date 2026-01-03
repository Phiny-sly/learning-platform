package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.common.security.SecurityUtils;
import com.phiny.labs.courseservice.dto.enrollment.CreateEnrollmentDTO;
import com.phiny.labs.courseservice.dto.enrollment.EnrollmentDTO;
import com.phiny.labs.courseservice.dto.enrollment.UpdateEnrollmentDTO;
import com.phiny.labs.courseservice.exception.AccessDeniedException;
import com.phiny.labs.courseservice.exception.InternalServerError;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Enrollment;
import com.phiny.labs.courseservice.client.NotificationServiceClient;
import com.phiny.labs.courseservice.client.UserServiceClient;
import com.phiny.labs.courseservice.repository.CourseRepository;
import com.phiny.labs.courseservice.repository.EnrollmentRepository;
import com.phiny.labs.courseservice.util.Util;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;
    @Autowired(required = false) private UserServiceClient userServiceClient;
    @Autowired(required = false) private NotificationServiceClient notificationServiceClient;

    public EnrollmentDTO create(UUID courseId, CreateEnrollmentDTO payload){
        // Security check: Ensure user can only enroll themselves (unless admin/instructor)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        
        // Convert Long userId to UUID (using least significant bits)
        UUID currentUserUUID = new UUID(0, currentUserId);
        
        // If studentId is provided, verify it matches current user (unless admin/instructor)
        if (payload.getStudentId() != null && !SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR")) {
            if (!payload.getStudentId().equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only enroll yourself");
            }
        } else {
            // Auto-set studentId to current user if not provided or if admin/instructor
            payload.setStudentId(currentUserUUID);
        }
        
        Enrollment enrollment = courseRepository.findById(courseId).map(
                course -> {
                    Enrollment e = modelMapper.map(payload, Enrollment.class);
                    e.setCourse(course);
                    return enrollmentRepository.save(e);
                }
        ).orElseThrow(()-> new NotFoundError(courseId));
        
        EnrollmentDTO dto = modelMapper.map(enrollment, EnrollmentDTO.class);
        dto.setCourseId(enrollment.getCourse().getId());
        
        // Send enrollment notification
        if (notificationServiceClient != null && userServiceClient != null) {
            try {
                UserServiceClient.UserDto user = userServiceClient.getUserById(currentUserId);
                if (user != null) {
                    NotificationServiceClient.CreateNotificationRequest notificationRequest = new NotificationServiceClient.CreateNotificationRequest();
                    notificationRequest.setUserId(user.getId());
                    notificationRequest.setEmail(user.getEmail());
                    notificationRequest.setTitle("Course Enrollment");
                    notificationRequest.setMessage("You have successfully enrolled in course '" + enrollment.getCourse().getTitle() + "'.");
                    notificationRequest.setType("COURSE_ENROLLMENT");
                    notificationServiceClient.createNotification(notificationRequest);
                }
            } catch (Exception e) {
                logger.error("Failed to send enrollment notification: {}", e.getMessage(), e);
            }
        }
        
        return dto;
    }

    public List<EnrollmentDTO> read(Integer page, Integer size, String[] sort, UUID courseId){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        UUID currentUserUUID = currentUserId != null ? new UUID(0, currentUserId) : null;
        
        Page<Enrollment> resultSet;
        if (courseId == null) {
            // If not admin/instructor, only show own enrollments
            if (!SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR") && currentUserUUID != null) {
                resultSet = enrollmentRepository.findAllByStudentId(currentUserUUID, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
            } else {
                resultSet = enrollmentRepository.findAll(PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
            }
        } else {
            // If not admin/instructor, only show own enrollments for the course
            if (!SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR") && currentUserUUID != null) {
                resultSet = enrollmentRepository.findAllByCourseIdAndStudentId(courseId, currentUserUUID, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
            } else {
                resultSet = enrollmentRepository.findAllByCourseId(courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
            }
        }
        return resultSet.stream().map(e -> {
            EnrollmentDTO dto = modelMapper.map(e, EnrollmentDTO.class);
            dto.setCourseId(e.getCourse().getId());
            return dto;
        }).toList();
    }

    public EnrollmentDTO readById(UUID id){
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(()->new NotFoundError(id));
        
        // Security check: Ensure user can only view their own enrollment (unless admin/instructor)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null && !SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR")) {
            UUID currentUserUUID = new UUID(0, currentUserId);
            if (!enrollment.getStudentId().equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only view your own enrollments");
            }
        }
        
        EnrollmentDTO dto = modelMapper.map(enrollment, EnrollmentDTO.class);
        dto.setCourseId(enrollment.getCourse().getId());
        return dto;
    }

    public EnrollmentDTO updateById(UUID id, UpdateEnrollmentDTO payload){
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        // Security check: Ensure user can only update their own enrollment (unless admin/instructor)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null && !SecurityUtils.isAdmin() && !SecurityUtils.hasAuthority("INSTRUCTOR")) {
            UUID currentUserUUID = new UUID(0, currentUserId);
            if (!enrollment.getStudentId().equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only update your own enrollments");
            }
        }

        try {objectMapper.readerForUpdating(enrollment).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}

        enrollmentRepository.save(enrollment);
        EnrollmentDTO dto = modelMapper.map(enrollment, EnrollmentDTO.class);
        dto.setCourseId(enrollment.getCourse().getId());
        return dto;
    }

    public void deleteById(UUID id){ enrollmentRepository.deleteById(id); }

}
