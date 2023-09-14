package com.turntabl.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntabl.labs.courseservice.dto.enrollment.CreateEnrollmentDTO;
import com.turntabl.labs.courseservice.dto.enrollment.EnrollmentDTO;
import com.turntabl.labs.courseservice.dto.enrollment.UpdateEnrollmentDTO;
import com.turntabl.labs.courseservice.dto.rating.CreateRatingDTO;
import com.turntabl.labs.courseservice.dto.rating.RatingDTO;
import com.turntabl.labs.courseservice.dto.tag.TagDTO;
import com.turntabl.labs.courseservice.dto.tag.UpdateTagDTO;
import com.turntabl.labs.courseservice.exception.InternalServerError;
import com.turntabl.labs.courseservice.exception.NotFoundError;
import com.turntabl.labs.courseservice.model.Enrollment;
import com.turntabl.labs.courseservice.model.Rating;
import com.turntabl.labs.courseservice.model.Tag;
import com.turntabl.labs.courseservice.repository.CourseRepository;
import com.turntabl.labs.courseservice.repository.EnrollmentRepository;
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
public class EnrollmentService {

    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public EnrollmentDTO create(UUID courseId, CreateEnrollmentDTO payload){
        // get UUID and role from jwt token (remote call)
        payload.setCreatedBy(UUID.randomUUID());
        return modelMapper.map(courseRepository.findById(courseId).map(
                course -> {
                    Enrollment enrollment = modelMapper.map(payload, Enrollment.class);
                    enrollment.setCourse(course);
                    return enrollmentRepository.save(enrollment);
                }
        ).orElseThrow(()-> new NotFoundError(courseId)), EnrollmentDTO.class);
    }

    public List<EnrollmentDTO> read(Integer page, Integer size, String[] sort, UUID courseId){
        Page<Enrollment> resultSet = courseId == null ? enrollmentRepository.findAll(PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ) : enrollmentRepository.findAllByCourseId(courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> modelMapper.map(t, EnrollmentDTO.class)).toList();
    }

    public EnrollmentDTO readById(UUID id){
        return modelMapper.map(enrollmentRepository.findById(id).orElseThrow(()->new NotFoundError()), EnrollmentDTO.class);
    }

    public EnrollmentDTO updateById(UUID id, UpdateEnrollmentDTO payload){

        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {objectMapper.readerForUpdating(enrollment).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}

        enrollmentRepository.save(enrollment);
        return modelMapper.map(enrollment, EnrollmentDTO.class);

    }

    public void deleteById(UUID id){ enrollmentRepository.deleteById(id); }

}
