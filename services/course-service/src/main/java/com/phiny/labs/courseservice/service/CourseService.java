package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.common.security.SecurityUtils;
import com.phiny.labs.courseservice.dto.course.CourseDTO;
import com.phiny.labs.courseservice.dto.course.CourseListDTO;
import com.phiny.labs.courseservice.dto.course.CreateCourseDTO;
import com.phiny.labs.courseservice.dto.course.UpdateCourseDTO;
import com.phiny.labs.courseservice.exception.AccessDeniedException;
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
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired(required = false)
    private UserServiceClient userServiceClient;
    @Autowired(required = false)
    private NotificationServiceClient notificationServiceClient;

    public CourseDTO create(CreateCourseDTO payload) {
        // Security check: Get current user ID and set as creator
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Authentication required");
        }

        // Convert Long userId to UUID (using least significant bits)
        UUID currentUserUUID = new UUID(0, currentUserId);

        // Set createdBy to current user (override any provided value for security)
        payload.setCreatedBy(currentUserUUID);

        Course course = courseRepository.save(modelMapper.map(payload, Course.class));
        CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);

        // Send notification if notification service is available
        if (notificationServiceClient != null && userServiceClient != null) {
            try {
                UserServiceClient.UserDto user = userServiceClient.getUserById(currentUserId);
                if (user != null) {
                    NotificationServiceClient.CreateNotificationRequest notificationRequest = new NotificationServiceClient.CreateNotificationRequest();
                    notificationRequest.setUserId(user.getId());
                    notificationRequest.setEmail(user.getEmail());
                    notificationRequest.setTitle("New Course Created");
                    notificationRequest.setMessage("Your course '" + course.getTitle() + "' has been created successfully.");
                    notificationRequest.setType("COURSE_CREATED");
                    notificationServiceClient.createNotification(notificationRequest);
                }
            } catch (Exception e) {
                logger.error("Failed to send course creation notification: {}", e.getMessage(), e);
            }
        }

        return courseDTO;
    }

    public List<CourseListDTO> read(Integer page, Integer size, String[] sort, String q, UUID categoryId) {
        Page<Course> resultSet = categoryId == null ? courseRepository.findByTitleContainingIgnoreCase(q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))))
                : courseRepository.findByCategories_IdAndTitleContainingIgnoreCase(categoryId, q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> modelMapper.map(t, CourseListDTO.class)).toList();
    }

    public CourseDTO readById(UUID id) {
        return modelMapper.map(courseRepository.findById(id).orElseThrow(NotFoundError::new), CourseDTO.class);
    }

    public CourseDTO updateById(UUID id, UpdateCourseDTO payload) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundError(id));

        // Security check: Ensure user can only update courses they created (unless admin)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null && !SecurityUtils.isAdmin()) {
            UUID currentUserUUID = new UUID(0, currentUserId);
            if (!course.getCreatedBy().equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only update courses you created");
            }
        }

        try {
            objectMapper.readerForUpdating(course).readValue(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());
        }
        courseRepository.save(course);
        return modelMapper.map(course, CourseDTO.class);
    }

    public void deleteById(UUID id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundError(id));

        // Security check: Ensure user can only delete courses they created (unless admin)
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null && !SecurityUtils.isAdmin()) {
            UUID currentUserUUID = new UUID(0, currentUserId);
            if (!course.getCreatedBy().equals(currentUserUUID)) {
                throw new AccessDeniedException("You can only delete courses you created");
            }
        }

        courseRepository.deleteById(id);
    }

    public void addCourseTags(UUID id, List<UUID> tagIds) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundError("course not found"));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        course.getTags().addAll(tags);
        courseRepository.save(course);
    }

    public void removeCourseTags(UUID id, List<UUID> tagIds) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundError("course not found"));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        tags.forEach(course.getTags()::remove);
        courseRepository.save(course);
    }

}
