package com.turntabl.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntabl.labs.courseservice.dto.course.CourseListDTO;
import com.turntabl.labs.courseservice.dto.tag.CreateTagDTO;
import com.turntabl.labs.courseservice.dto.tag.TagDTO;
import com.turntabl.labs.courseservice.dto.tag.TagListDTO;
import com.turntabl.labs.courseservice.dto.tag.UpdateTagDTO;
import com.turntabl.labs.courseservice.exception.InternalServerError;
import com.turntabl.labs.courseservice.exception.NotFoundError;
import com.turntabl.labs.courseservice.model.Course;
import com.turntabl.labs.courseservice.model.Tag;
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
public class TagService {

    @Autowired private TagRepository tagRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public TagDTO create(CreateTagDTO payload){
        // get UUID and role from jwt token (remote call)
        payload.setCreatedBy(UUID.randomUUID());
        return modelMapper.map(tagRepository.save(modelMapper.map(payload, Tag.class)), TagDTO.class);
    }

    public List<TagListDTO> read(Integer page, Integer size, String[] sort, String q, UUID courseId){
        Page<Tag> resultSet = courseId == null ? tagRepository.findAll(q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))) :
                tagRepository.findAllByCourseId(q, courseId, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort))));
        return resultSet.stream().map(t -> modelMapper.map(t, TagListDTO.class)).toList();
    }

    public TagDTO readById(UUID id){
        return modelMapper.map(tagRepository.findById(id).orElseThrow(()->new NotFoundError()), TagDTO.class);
    }

    public TagDTO updateById(UUID id, UpdateTagDTO payload){

        Tag tag = tagRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {objectMapper.readerForUpdating(tag).readValue(objectMapper.writeValueAsString(payload));}
        catch (JsonProcessingException e) { throw new InternalServerError("update failed, something went wrong -> " + e.getMessage());}

        tagRepository.save(tag);
        return modelMapper.map(tag, TagDTO.class);

    }

    public void deleteById(UUID id){ tagRepository.deleteById(id); }

}
