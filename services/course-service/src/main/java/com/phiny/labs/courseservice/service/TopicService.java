package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.common.exception.ServiceException;
import com.phiny.labs.courseservice.dto.topic.CreateTopicDTO;
import com.phiny.labs.courseservice.dto.topic.TopicDTO;
import com.phiny.labs.courseservice.dto.topic.TopicListDTO;
import com.phiny.labs.courseservice.dto.topic.UpdateTopicDTO;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Topic;
import com.phiny.labs.courseservice.repository.CourseRepository;
import com.phiny.labs.courseservice.repository.TopicRepository;
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
public class TopicService {
    @Autowired private TopicRepository topicRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public TopicDTO create(UUID courseId, CreateTopicDTO payload){
        Topic topic = courseRepository.findById(courseId).map(
                course -> {
                    Topic t = modelMapper.map(payload, Topic.class);
                    t.setCourse(course);
                    return topicRepository.save(t);
                }
        ).orElseThrow(()-> new NotFoundError(courseId));
        TopicDTO dto = modelMapper.map(topic, TopicDTO.class);
        dto.setCourseId(topic.getCourse().getId());
        return dto;
    }

    public List<TopicListDTO> read(Integer page, Integer size, String[] sort, String q, UUID courseId){
        Page<Topic> resultSet = courseId == null ? topicRepository.findAll(q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ) : topicRepository.findAllByCourseId(q, courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> {
            TopicListDTO dto = modelMapper.map(t, TopicListDTO.class);
            dto.setCourseId(t.getCourse().getId());
            return dto;
        }).toList();
    }

    public TopicDTO readById(UUID id){
        Topic topic = topicRepository.findById(id).orElseThrow(()->new NotFoundError());
        TopicDTO dto = modelMapper.map(topic, TopicDTO.class);
        dto.setCourseId(topic.getCourse().getId());
        return dto;
    }

    public TopicDTO updateById(UUID id, UpdateTopicDTO payload){

        Topic topic = topicRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {
            objectMapper.readerForUpdating(topic).readValue(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to update topic", e);
        }

        topicRepository.save(topic);
        TopicDTO dto = modelMapper.map(topic, TopicDTO.class);
        dto.setCourseId(topic.getCourse().getId());
        return dto;

    }

    public void deleteById(UUID id){ topicRepository.deleteById(id); }

}
