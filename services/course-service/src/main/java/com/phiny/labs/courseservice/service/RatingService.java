package com.phiny.labs.courseservice.service;

import com.phiny.labs.courseservice.dto.rating.CreateRatingDTO;
import com.phiny.labs.courseservice.dto.rating.RatingDTO;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Rating;
import com.phiny.labs.courseservice.repository.CourseRepository;
import com.phiny.labs.courseservice.repository.RatingRepository;
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
public class RatingService {

    @Autowired private RatingRepository ratingRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ModelMapper modelMapper;

    public RatingDTO create(UUID courseId, CreateRatingDTO payload){
        Rating rating = courseRepository.findById(courseId).map(
                course -> {
                    Rating r = modelMapper.map(payload, Rating.class);
                    r.setCourse(course);
                    return ratingRepository.save(r);
                }
        ).orElseThrow(()-> new NotFoundError(courseId));
        RatingDTO dto = modelMapper.map(rating, RatingDTO.class);
        dto.setCourseId(rating.getCourse().getId());
        return dto;
    }

    public List<RatingDTO> read(Integer page, Integer size, String[] sort, UUID courseId){
        Page<Rating> resultSet = courseId == null ? ratingRepository.findAll(PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ) : ratingRepository.findAllByCourseId(courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(r -> {
            RatingDTO dto = modelMapper.map(r, RatingDTO.class);
            dto.setCourseId(r.getCourse().getId());
            return dto;
        }).toList();
    }

    public RatingDTO readById(UUID id){
        Rating rating = ratingRepository.findById(id).orElseThrow(()->new NotFoundError());
        RatingDTO dto = modelMapper.map(rating, RatingDTO.class);
        dto.setCourseId(rating.getCourse().getId());
        return dto;
    }

    public void deleteById(UUID id){ ratingRepository.deleteById(id); }

}
