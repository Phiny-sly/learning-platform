package com.turntabl.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntabl.labs.courseservice.dto.course.CourseDTO;
import com.turntabl.labs.courseservice.dto.course.CourseListDTO;
import com.turntabl.labs.courseservice.dto.course.CreateCourseDTO;
import com.turntabl.labs.courseservice.dto.course.UpdateCourseDTO;
import com.turntabl.labs.courseservice.dto.tag.CreateTagDTO;
import com.turntabl.labs.courseservice.exception.InternalServerError;
import com.turntabl.labs.courseservice.exception.NotFoundError;
import com.turntabl.labs.courseservice.model.Course;
import com.turntabl.labs.courseservice.model.Tag;
import com.turntabl.labs.courseservice.repository.CourseRepository;
import com.turntabl.labs.courseservice.repository.TagRepository;
import com.turntabl.labs.courseservice.util.Util;
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

    public CourseDTO create(CreateCourseDTO payload){
        // get UUID and role from jwt token (remote call)
        payload.setCreatedBy(UUID.randomUUID());
        return modelMapper.map(courseRepository.save(modelMapper.map(payload, Course.class)), CourseDTO.class);
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
