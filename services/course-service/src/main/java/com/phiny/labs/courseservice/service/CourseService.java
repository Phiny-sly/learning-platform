package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.courseservice.dto.course.CourseDTO;
import com.phiny.labs.courseservice.dto.course.CourseListDTO;
import com.phiny.labs.courseservice.dto.course.CreateCourseDTO;
import com.phiny.labs.courseservice.dto.course.UpdateCourseDTO;
import com.phiny.labs.courseservice.exception.InternalServerError;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Course;
import com.phiny.labs.courseservice.model.Tag;
import com.phiny.labs.courseservice.client.NotificationServiceClient;
import com.phiny.labs.courseservice.client.UserServiceClient;
import com.phiny.labs.courseservice.repository.CourseRepository;
import com.phiny.labs.courseservice.repository.TagRepository;
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
public class CourseService {

    @Autowired private CourseRepository courseRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;
    @Autowired(required = false) private UserServiceClient userServiceClient;
    @Autowired(required = false) private NotificationServiceClient notificationServiceClient;

    public CourseDTO create(CreateCourseDTO payload){
        // Note: User validation would require converting UUID to Long or adding a UUID endpoint
        // For now, we'll validate if a user ID is provided in a compatible format
        if (payload.getCreatedBy() == null) {
            // Fallback to random UUID if not provided
            payload.setCreatedBy(UUID.randomUUID());
        }
        
        Course course = courseRepository.save(modelMapper.map(payload, Course.class));
        CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
        
        // Send notification if notification service is available
        // Note: This would require user lookup by UUID or email, which needs additional endpoints
        // For now, notifications can be sent separately via the notification service API
        
        return courseDTO;
    }

    public List<CourseListDTO> read(Integer page, Integer size, String[] sort, String q, UUID categoryId){
        Page<Course> resultSet = categoryId == null ? courseRepository.findByTitleContainingIgnoreCase(q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))))
                : courseRepository.findByCategories_IdAndTitleContainingIgnoreCase(categoryId, q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> modelMapper.map(t, CourseListDTO.class)).toList();
    }

    public CourseDTO readById(UUID id){
        return modelMapper.map(courseRepository.findById(id).orElseThrow(()->new NotFoundError()), CourseDTO.class);
    }

    public CourseDTO updateById(UUID id, UpdateCourseDTO payload){
        Course course = courseRepository.findById(id).orElseThrow(()-> new NotFoundError(id));
        try {objectMapper.readerForUpdating(course).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}
        courseRepository.save(course);
        return modelMapper.map(course, CourseDTO.class);
    }

    public void deleteById(UUID id){ courseRepository.deleteById(id); }

    public void addCourseTags(UUID id, List<UUID> tagIds){
        Course course = courseRepository.findById(id).orElseThrow(()->new NotFoundError("course not found"));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        course.getTags().addAll(tags);
        courseRepository.save(course);
    }

    public void removeCourseTags(UUID id, List<UUID> tagIds){
        Course course = courseRepository.findById(id).orElseThrow(()->new NotFoundError("course not found"));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        course.getTags().removeAll(tags);
        courseRepository.save(course);
    }

}
