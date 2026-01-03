package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.common.exception.ServiceException;
import com.phiny.labs.courseservice.dto.tag.CreateTagDTO;
import com.phiny.labs.courseservice.dto.tag.TagDTO;
import com.phiny.labs.courseservice.dto.tag.TagListDTO;
import com.phiny.labs.courseservice.dto.tag.UpdateTagDTO;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Tag;
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

        try {
            objectMapper.readerForUpdating(tag).readValue(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to update tag", e);
        }

        tagRepository.save(tag);
        return modelMapper.map(tag, TagDTO.class);

    }

    public void deleteById(UUID id){ tagRepository.deleteById(id); }

}
