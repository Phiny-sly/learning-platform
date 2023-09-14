package com.turntabl.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntabl.labs.courseservice.dto.rating.RatingDTO;
import com.turntabl.labs.courseservice.dto.topic.CreateTopicDTO;
import com.turntabl.labs.courseservice.dto.topic.TopicDTO;
import com.turntabl.labs.courseservice.dto.topic.TopicListDTO;
import com.turntabl.labs.courseservice.dto.topic.UpdateTopicDTO;
import com.turntabl.labs.courseservice.exception.InternalServerError;
import com.turntabl.labs.courseservice.exception.NotFoundError;
import com.turntabl.labs.courseservice.model.Rating;
import com.turntabl.labs.courseservice.model.Topic;
import com.turntabl.labs.courseservice.repository.CourseRepository;
import com.turntabl.labs.courseservice.repository.TopicRepository;
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
public class TopicService {
    @Autowired private TopicRepository topicRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public TopicDTO create(UUID courseId, CreateTopicDTO payload){
        // get UUID and role from jwt token (remote call)
        payload.setCreatedBy(UUID.randomUUID());
        return modelMapper.map(courseRepository.findById(courseId).map(
                course -> {
                    Topic topic = modelMapper.map(payload, Topic.class);
                    topic.setCourse(course);
                    return topicRepository.save(topic);
                }
        ).orElseThrow(()-> new NotFoundError(courseId)), TopicDTO.class);
    }

    public List<TopicListDTO> read(Integer page, Integer size, String[] sort, String q, UUID courseId){
        Page<Topic> resultSet = courseId == null ? topicRepository.findAll(q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ) : topicRepository.findAllByCourseId(q, courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> modelMapper.map(t, TopicListDTO.class)).toList();
    }

    public TopicDTO readById(UUID id){
        return modelMapper.map(topicRepository.findById(id).orElseThrow(()->new NotFoundError()), TopicDTO.class);
    }

    public TopicDTO updateById(UUID id, UpdateTopicDTO payload){

        Topic topic = topicRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {objectMapper.readerForUpdating(topic).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}

        topicRepository.save(topic);
        return modelMapper.map(topic, TopicDTO.class);

    }

    public void deleteById(UUID id){ topicRepository.deleteById(id); }

}
