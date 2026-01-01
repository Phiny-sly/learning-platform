package com.phiny.labs.courseservice.controller;

import com.phiny.labs.courseservice.dto.category.CategoryDTO;
import com.phiny.labs.courseservice.dto.category.CategoryListDTO;
import com.phiny.labs.courseservice.dto.category.CreateCategoryDTO;
import com.phiny.labs.courseservice.dto.category.UpdateCategoryDTO;
import com.phiny.labs.courseservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    @Autowired private CategoryService categoryService;

    @PostMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDTO create(@Valid @RequestBody CreateCategoryDTO payload){
        return categoryService.create(payload);
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    List<CategoryListDTO> read(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){ return categoryService.read(page, size, sort, q); }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    CategoryDTO readById(@PathVariable("id") UUID id){
        return categoryService.readById(id);
    }

    @PatchMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    CategoryDTO updateById(@PathVariable("id") UUID id, @RequestBody UpdateCategoryDTO payload){
        return categoryService.updateById(id, payload);
    }

    @PutMapping(value = {"{id}/courses", "/{id}/courses/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    void addCategoryCourses(@PathVariable("id") UUID id, @RequestBody List<UUID> courseIds){
        categoryService.addCategoryCourses(id, courseIds);
    }

    @DeleteMapping(value = {"{id}/courses", "/{id}/courses/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategoryCourses(@PathVariable("id") UUID id, @RequestBody List<UUID> courseIds){
        categoryService.removeCategoryCourses(id, courseIds);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeById(@PathVariable("id") UUID id){
        categoryService.deleteById(id);
    }

}
