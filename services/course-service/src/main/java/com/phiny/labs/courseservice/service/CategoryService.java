package com.phiny.labs.courseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phiny.labs.common.exception.ServiceException;
import com.phiny.labs.courseservice.dto.category.CategoryDTO;
import com.phiny.labs.courseservice.dto.category.CategoryListDTO;
import com.phiny.labs.courseservice.dto.category.CreateCategoryDTO;
import com.phiny.labs.courseservice.dto.category.UpdateCategoryDTO;
import com.phiny.labs.courseservice.exception.NotFoundError;
import com.phiny.labs.courseservice.model.Category;
import com.phiny.labs.courseservice.model.Course;
import com.phiny.labs.courseservice.repository.CategoryRepository;
import com.phiny.labs.courseservice.repository.CourseRepository;
import com.phiny.labs.courseservice.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ModelMapper modelMapper;

    public CategoryDTO create(CreateCategoryDTO payload){
        // get UUID and role from jwt token (remote call)
        payload.setCreatedBy(UUID.randomUUID());
        return modelMapper.map(categoryRepository.save(modelMapper.map(payload, Category.class)), CategoryDTO.class);
    }

    public List<CategoryListDTO> read(Integer page, Integer size, String[] sort, String q){
        return categoryRepository.findAll(
                q, PageRequest.of(page, size, Sort.by(Util.generateSortOrders(sort)))
        ).stream().map(t -> modelMapper.map(t, CategoryListDTO.class)).toList();
    }

    public CategoryDTO readById(UUID id){
        return modelMapper.map(categoryRepository.findById(id).orElseThrow(()->new NotFoundError()), CategoryDTO.class);
    }

    public CategoryDTO updateById(UUID id, UpdateCategoryDTO payload){

        Category category = categoryRepository.findById(id).orElseThrow(()-> new NotFoundError(id));

        try {
            objectMapper.readerForUpdating(category).readValue(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to update category", e);
        }

        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDTO.class);

    }

    public void addCategoryCourses(UUID id, List<UUID> courseIds){
        Category category = categoryRepository.findById(id).orElseThrow(()->new NotFoundError("category not found"));
        List<Course> courses = courseRepository.findAllById(courseIds);
        category.getCourses().addAll(courses);
        courses.forEach(course -> course.getCategories().add(category));
        categoryRepository.save(category);
    }

    public void removeCategoryCourses(UUID id, List<UUID> courseIds){
        Category category = categoryRepository.findById(id).orElseThrow(()->new NotFoundError("category not found"));
        List<Course> courses = courseRepository.findAllById(courseIds);
        category.getCourses().removeAll(courses);
        courses.forEach(course -> course.getCategories().remove(category));
        categoryRepository.save(category);
    }

    public void deleteById(UUID id){ categoryRepository.deleteById(id); }

}
