package com.turntabl.labs.courseservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntabl.labs.courseservice.dto.enrollment.EnrollmentDTO;
import com.turntabl.labs.courseservice.dto.rating.CreateRatingDTO;
import com.turntabl.labs.courseservice.dto.rating.RatingDTO;
import com.turntabl.labs.courseservice.exception.NotFoundError;
import com.turntabl.labs.courseservice.model.Enrollment;
import com.turntabl.labs.courseservice.model.Rating;
import com.turntabl.labs.courseservice.repository.CourseRepository;
import com.turntabl.labs.courseservice.repository.RatingRepository;
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
public class RatingService {

    @Autowired private RatingRepository ratingRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public RatingDTO create(UUID courseId, CreateRatingDTO payload){
        // get UUID and role from jwt token (remote call)
        payload.setCreatedBy(UUID.randomUUID());
        return modelMapper.map(courseRepository.findById(courseId).map(
                course -> {
                    Rating rating = modelMapper.map(payload, Rating.class);
                    rating.setCourse(course);
                    return ratingRepository.save(rating);
                }
        ).orElseThrow(()-> new NotFoundError(courseId)), RatingDTO.class);
    }

    public List<RatingDTO> read(Integer page, Integer size, String[] sort, UUID courseId){
        Page<Rating> resultSet = courseId == null ? ratingRepository.findAll(PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ) : ratingRepository.findAllByCourseId(courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> modelMapper.map(t, RatingDTO.class)).toList();
    }

    public RatingDTO readById(UUID id){
        return modelMapper.map(ratingRepository.findById(id).orElseThrow(()->new NotFoundError()), RatingDTO.class);
    }

    public void deleteById(UUID id){ ratingRepository.deleteById(id); }

}
