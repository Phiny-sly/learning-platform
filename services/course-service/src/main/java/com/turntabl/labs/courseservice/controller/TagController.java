package com.turntabl.labs.courseservice.controller;

import com.turntabl.labs.courseservice.dto.tag.CreateTagDTO;
import com.turntabl.labs.courseservice.dto.tag.TagDTO;
import com.turntabl.labs.courseservice.dto.tag.TagListDTO;
import com.turntabl.labs.courseservice.dto.tag.UpdateTagDTO;
import com.turntabl.labs.courseservice.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/tags")
@RequiredArgsConstructor
public class TagController {

    @Autowired private TagService tagService;

    @PostMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    TagDTO create(@RequestBody CreateTagDTO payload){
        return tagService.create(payload);
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    List<TagListDTO> read(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(defaultValue = "created, asc") String[] sort
    ){
        return tagService.read(page, size, sort, q, courseId);
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.OK)
    TagDTO readById(@PathVariable("id") UUID id){
        return tagService.readById(id);
    }

    @PatchMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    TagDTO updateById(@PathVariable("id") UUID id, @RequestBody UpdateTagDTO payload){
        return tagService.updateById(id, payload);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeById(@PathVariable("id") UUID id){
        tagService.deleteById(id);
    }

}
