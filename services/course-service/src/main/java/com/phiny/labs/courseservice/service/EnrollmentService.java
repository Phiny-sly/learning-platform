package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.courseservice.dto.enrollment.CreateEnrollmentDTO;
import com.phiny.labs.courseservice.dto.enrollment.EnrollmentDTO;
import com.phiny.labs.courseservice.dto.enrollment.UpdateEnrollmentDTO;
import com.phiny.labs.courseservice.exception.InternalServerError;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Enrollment;
import com.phiny.labs.courseservice.client.NotificationServiceClient;
import com.phiny.labs.courseservice.client.UserServiceClient;
import com.phiny.labs.courseservice.repository.CourseRepository;
import com.phiny.labs.courseservice.repository.EnrollmentRepository;
import com.phiny.labs.courseservice.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {

    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;
    @Autowired(required = false) private UserServiceClient userServiceClient;
    @Autowired(required = false) private NotificationServiceClient notificationServiceClient;

    public EnrollmentDTO create(UUID courseId, CreateEnrollmentDTO payload){
        // Note: User validation would require UUID to Long conversion or additional endpoints
        // For now, we'll proceed with enrollment and send notification if possible
        
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
        // Note: This requires user lookup which needs UUID-compatible endpoints
        // For now, notifications can be sent via the notification service API with user email/ID
        
        return dto;
    }

    public List<EnrollmentDTO> read(Integer page, Integer size, String[] sort, UUID courseId){
        Page<Enrollment> resultSet = courseId == null ? enrollmentRepository.findAll(PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ) : enrollmentRepository.findAllByCourseId(courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(e -> {
            EnrollmentDTO dto = modelMapper.map(e, EnrollmentDTO.class);
            dto.setCourseId(e.getCourse().getId());
            return dto;
        }).toList();
    }

    public EnrollmentDTO readById(UUID id){
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(()->new NotFoundError());
        EnrollmentDTO dto = modelMapper.map(enrollment, EnrollmentDTO.class);
        dto.setCourseId(enrollment.getCourse().getId());
        return dto;
    }

    public EnrollmentDTO updateById(UUID id, UpdateEnrollmentDTO payload){

        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {objectMapper.readerForUpdating(enrollment).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}

        enrollmentRepository.save(enrollment);
        EnrollmentDTO dto = modelMapper.map(enrollment, EnrollmentDTO.class);
        dto.setCourseId(enrollment.getCourse().getId());
        return dto;

    }

    public void deleteById(UUID id){ enrollmentRepository.deleteById(id); }

}
